package services.search

import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import daos.{PageWordDAO, IndexablePageDAO}
import models.PageWord
import play.api.libs.ws.WS
import play.api.libs.ws.ning.NingWSClient
import utils.{ElasticsearchConfigs, Environment}
import java.net.URLEncoder

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Promise

/**
 * Created by nelsonpascoal on 2015/07/29.
 */
object Updater {
  val props = Props(classOf[Updater])

  case class NormalizeToken(
    token:          String,
    start_offset:   Int,
    end_offset:     Int,
    `type`:         String,
    position:       Int
  )
  case class NormalizeTokens(tokens: List[NormalizeToken])

  implicit val normalizeTokenFormat  = Json.format[NormalizeToken]
  implicit val normalizeTokensFormat = Json.format[NormalizeTokens]
}

class Updater extends Actor with ActorLogging with Environment with ElasticsearchConfigs {
  import Updater._
  import Scraper.{UpdateSearch, Done, PageData, Failed}

  implicit val wsClient = NingWSClient()

  val pageDao   = new IndexablePageDAO()
  val wordDao   = new PageWordDAO()

  def receive = {
    case u: UpdateSearch => normaliseText(u.requestor, u.pageId, u.pageData)
  }

  def normaliseText(requestor: ActorRef, pageId: Long, p: PageData) = {
    val includeMeta = List("description", "keywords")

    val text = s"${p.title} ${p.content} " + p.metaTags.getOrElse(Nil).filter { m =>
      includeMeta.contains(m.name)
    }.map(_.content).mkString(" ")

    val queryText = URLEncoder.encode(text, "UTF-8")

    WS.clientUrl(s"$requestUri$queryText").get map { r =>
      val tokens = Json.parse(r.body).as[NormalizeTokens].tokens filter(_.`type` == "<ALPHANUM>") map(_.token)

      updatePageWords(pageId, tokens) map { _ =>
        updatePageDescriptions(p, pageId) map { r =>
          if(r.isEmpty) log.warning(s"Failed to update page descriptions for page $pageId")
          else log.info(s"Successfully updated page descriptions for $pageId")

          requestor ! Done(pageId)
        }
      } recover {
        case e: Throwable =>
          log.error(e, e.getMessage)
          requestor ! Failed
      }
    } recover {
      case e: Throwable =>
        log.error(e, e.getMessage)
        requestor ! Failed
    }
  }

  def updatePageWords(pageId: Long, tokens: List[String]) = {
    val promise = Promise[Unit]()

    def createWords(ws: List[String], created: Int = 0): Unit = ws match {
      case t::ts    =>
        wordDao.create(PageWord(indexablePageId = pageId, word = t)) map { _ =>
          createWords(ts, created + 1)
        } recover {
          case e: Throwable => promise failure e
        }
      case _        =>
        log.info(s"ReIndexed $created of ${tokens.size} for page id $pageId")
        promise success Unit
        ()
    }

    wordDao.removeForPage(pageId) map { i =>
      log.info(s"Removed $i old index words for page id $pageId")

      createWords(tokens)
    }

    promise.future
  }

  def updatePageDescriptions(p: PageData, pageId: Long) = {
    val description = p.metaTags match {
      case Some(metaTags) => {
        if(metaTags.exists(_.name == "description"))
          metaTags.find(_.name == "description").get.content
        else
          p.content
      }
      case _              => p.content
    }

    pageDao.updateSearchDescriptions(pageId, p.title, description)
  }
}
