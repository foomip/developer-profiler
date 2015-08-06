package services.search

import java.net.URLEncoder

import akka.pattern.pipe
import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import com.google.inject.Inject
import daos.{IndexablePageDAO, PageWordDAO}
import models.SearchStats
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import play.api.libs.ws.ning.NingWSClient
import utils.{ElasticsearchConfigs, Environment}
import scala.concurrent.{Future, Promise}

import play.api.libs.json._

/**
 * Created by nelsonpascoal on 2015/08/01.
 */
object Finder {
  val props = Props(classOf[Finder])

  case class Search(text: String)
}

class Finder @Inject() extends Actor with ActorLogging with Environment with ElasticsearchConfigs {
  import Finder.Search
  import Supervisor.{ReIndexing, IndexingComplete}
  import Updater._

  implicit val wsClient = NingWSClient()

  var outStandingRequests: List[(ActorRef, Search)] = Nil

  lazy val wordDAO = new PageWordDAO()
  lazy val pageDAO = new IndexablePageDAO()

  def reIndexing: Receive = {
    case s: Search          => outStandingRequests = outStandingRequests :+ (sender(), s)
    case IndexingComplete   => completeOutstanding map { _ =>
      context.become(running)
    } recover {
      case e: Throwable =>
        log.error(e, e.getMessage)
        outStandingRequests = Nil
        context.become(running)
    }
  }

  def running: Receive = {
    case s: Search          => pipe( search(s) ) to sender()
    case ReIndexing         => context.become(reIndexing)
  }

  def receive = running

  def completeOutstanding = {
    val promise = Promise[Unit]()

    promise success Unit

    promise.future
  }

  def search(s: Search) = {
    val queryText = URLEncoder.encode(s.text, "UTF-8")

    WS.clientUrl(s"$requestUri$queryText").get flatMap { r =>
      val tokens = Json.parse(r.body).as[NormalizeTokens].tokens filter(_.`type` == "<ALPHANUM>") map(_.token)

      wordDAO.wordMatches(tokens) flatMap { wordStats =>
        pageDAO.pageDescriptionsFor(wordStats map(_._1)) map { pageInfos =>
          wordStats zip pageInfos map { x =>
            val ((_, total, hits, score),(pageId, title, description)) = x
            SearchStats(pageId, total, hits, score, title.get, description.get)
          }
        }
      }
    }
  }
}
