package services.search

import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import models.IndexablePage
import sys.process._
import scala.language.postfixOps

import play.api.libs.json._

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
object Scraper {
  def props(url: String, updater: ActorRef) = Props(classOf[Scraper], url, updater)

  case class Done(id: Long)
  case class Failed(id: Long)
  case class MetaTag(name: Option[String] = None, property: Option[String] = None, content: String)
  case class PageData(title: String, content: String, metaTags: Option[List[MetaTag]])
  case class UpdateSearch(requestor: ActorRef, pageData: PageData, pageId: Long)

  implicit val metaTagFormat  = Json.format[MetaTag]
  implicit val pageDataFormat = Json.format[PageData]
}

class Scraper(url: String, updater: ActorRef) extends Actor with ActorLogging {
  import Scraper._

  val startMarker = ">>>>>>>>>>>>> OUTPUT START"
  val endMarker   = ">>>>>>>>>>>>> OUTPUT END"

  def receive = {
    case p: IndexablePage => {
      val client = sender()

      log.info("SCRAPE REQUEST!")

      val reqString = s"node scripts/site_scraper $url ${p.path}" !!
      val start     = reqString.indexOf(startMarker) + startMarker.length
      val end       = reqString.indexOf(endMarker)

      val pageData = Json.parse(reqString.substring(start, end).trim).as[PageData]

      updater ! UpdateSearch(client, pageData, p.id.get)
    }
  }
}
