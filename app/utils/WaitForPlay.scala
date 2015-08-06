package utils

import akka.actor.ActorSystem
import play.api.Play
import scala.concurrent.duration._
import scala.language.postfixOps

import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by nelsonpascoal on 2015/08/02.
 */
trait WaitForPlay {
  val system: ActorSystem

  def waitForPlay(cb: => Unit): Unit = {
    try {
      Play.current // throws exception if Ply environment is running yet
      cb
    }
    catch {
      case e: RuntimeException => {
        system.scheduler.scheduleOnce(500 milliseconds){ waitForPlay(cb) }
      }
    }
  }
}
