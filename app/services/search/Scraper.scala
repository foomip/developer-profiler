package services.search

import akka.actor.{ActorLogging, Actor, Props}
import models.IndexablePage

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
object Scraper {
  def props(url: String) = Props(classOf[Scraper], url)

  case class Done(id: Long)
  case class Failed(id: Long)
}

class Scraper(url: String) extends Actor with ActorLogging {
  import Scraper.Done

  def receive = {
    case p: IndexablePage => {
      val client = sender()

      log.info("SCRAPE REQUEST!")
      client ! Done(p.id.get)
    }
  }
}
