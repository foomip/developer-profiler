package services.search

import akka.pattern.ask
import akka.actor.{ActorLogging, Actor, Props}
import akka.util.Timeout
import scala.language.postfixOps
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by nelsonpascoal on 2015/07/25.
 */
object Supervisor {
  val props = Props(classOf[Supervisor])

  object Run
}

class Supervisor extends Actor with ActorLogging {
  import Supervisor.Run
  import Indexer.{ReIndex, Done, Busy}

  val indexer = context.actorOf(Indexer.props, "ReIndexer")

  def runReIndex(implicit t: Timeout) = indexer ? ReIndex

  def runAndSchedule(): Unit = {
    def schedule = {
      context.system.scheduler.scheduleOnce(12 seconds)(runAndSchedule())
    }

    runReIndex(1 hour) map {
      case Done =>
        if(play.api.Play.isProd(play.api.Play.current)) schedule
        else {
          log.info("Not production - stopping after single run")
          stop()
          context.system.shutdown()
        }
      case Busy => schedule
      case x    =>
        log.warning(s"Received unexpected response from ReIndexer actor ($x)?? re-scheduling anyway.")
        schedule
    } recover {
      case e: Throwable =>
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
