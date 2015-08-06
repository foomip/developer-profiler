package services.search

import akka.pattern.ask
import akka.actor.{ActorLogging, Actor, Props}
import akka.util.Timeout
import scala.language.postfixOps
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play

/**
 * Created by nelsonpascoal on 2015/07/25.
 */
object Supervisor {
  val props = Props(classOf[Supervisor])

  case object Run
  case object ReIndexing
  case object IndexingComplete
}

class Supervisor extends Actor with ActorLogging {
  import Supervisor.{Run, ReIndexing, IndexingComplete}
  import Indexer.{ReIndex, Done, Busy}

  val finder =  context.system.actorSelection("/user/SearchFinder")
  val indexer = context.actorOf(Indexer.props, "ReIndexer")

  def runReIndex(implicit t: Timeout) = indexer ? ReIndex

  def runAndSchedule(): Unit = {
    finder ! ReIndexing

    def schedule = {
      context.system.scheduler.scheduleOnce(12 seconds)(runAndSchedule())
    }

    runReIndex(1 hour) map {
      case Done =>
        finder ! IndexingComplete
        if(Play.isProd(Play.current)) schedule
        else {
          log.info("Not production - stopping after single run")
          stop()
        }
      case Busy =>
        finder ! IndexingComplete
        schedule
      case x    =>
        finder ! IndexingComplete
        log.warning(s"Received unexpected response from ReIndexer actor ($x)?? re-scheduling anyway.")
        schedule
    } recover {
      case e: Throwable =>
        finder ! IndexingComplete
        log.error(s"Search index routine failed - ${e.getMessage}", e)
        schedule
    }
  }

  def stop() = {
    context.stop(indexer)
    context.stop(self)
  }

  def receive = {
    case Run  => runAndSchedule()
  }
}
