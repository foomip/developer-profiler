package services.search

import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import daos.IndexablePageDAO

import play.api.libs.concurrent.Execution.Implicits._
import utils.Environment
import play.api.Play.current

/**
 * Created by nelsonpascoal on 2015/07/25.
 */
object Indexer {
  val props = Props(classOf[Indexer])

  object ReIndex
  object Done
  object Busy
}

class Indexer extends Actor with ActorLogging with Environment {
  import Indexer.{Done, ReIndex, Busy}

  var client = self

  val dao = new IndexablePageDAO()
  val url = play.api.Play.configuration.getString(s"indexer.$environment.url").getOrElse("http://127.0.0.1:9000")

  lazy val scrapers: Seq[ActorRef] = (1 to 2) map { i => context.actorOf(Scraper.props(url), s"Scraper$i") }

  def idle: Receive = {
    case ReIndex =>
      client = sender()
      startIndexing
  }

  def startIndexing = {
    dao.findAll map { pages =>
      val awaitingIds = Set[Long]() ++ pages.map { _.id.get }
      val retries = pages.foldLeft(Map[Long, Int]()) { (m, p) => m + (p.id.get -> 1) }

      pages zip (Stream continually scrapers).flatten foreach { x =>
        val (page, scraper) = x
        scraper ! page
      }

      context.become(fetchingIndexes(awaitingIds, retries))
    }
  }

  def fetchingIndexes(awaitingIds: Set[Long], retries: Map[Long, Int]): Receive = {
    case ReIndex              => sender ! Busy
    case d: Scraper.Done      => markPageComplete(d.id, awaitingIds, retries)
    case f: Scraper.Failed    => {
      if(retries(f.id) < 3)
        context.become(fetchingIndexes(awaitingIds, retries.updated(f.id, retries(f.id) + 1)))
      else
        markPageComplete(f.id, awaitingIds, retries)
    }
  }

  def markPageComplete(id: Long, awaitingIds: Set[Long], retries: Map[Long, Int]) = {
    val ids = awaitingIds - id

    if(ids.isEmpty) {
      log.info("Re-Index process complete, waiting for new requests")
      context.become(idle)
      client ! Done
    }
    else context.become(fetchingIndexes(ids, retries))
  }

  def receive = idle
}
