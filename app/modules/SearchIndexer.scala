package modules

import akka.actor.ActorSystem
import com.google.inject.{Singleton, AbstractModule}
import play.api.{Play, Logger}
import play.api.db.slick.DatabaseConfigProvider
import services.search.Supervisor
import slick.driver.JdbcProfile
import sys.process._
import scala.language.postfixOps
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by nelsonpascoal on 2015/07/13.
 */

object SearchIndexer {
  val system = ActorSystem(s"SearchIndex")
}

@Singleton
class SearchIndexer extends AbstractModule {
  import SearchIndexer.system
  import services.search.Supervisor.Run

  def configure() = {
    val result = { "which phantomjs" ! }

    if (result != 0) {
      println("Server needs to have PhantomJS executable installed, cannot start Search ReIndexer")
    }
    else {
      println("Search Indexer - waiting for Play application to start")

      waitForPlay {
        Logger.info("Play started - spinning up Search Indexer")
        val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
        val searchSystem = system.actorOf(Supervisor.props, "SearchSupervisor")

        searchSystem ! Run
      }
    }
  }

  def waitForPlay(cb: => Unit): Unit = {
    try {
      play.api.Play.current
      cb
    }
    catch {
      case e: RuntimeException => {
        system.scheduler.scheduleOnce(500 milliseconds){ waitForPlay(cb) }
      }
    }
  }
}
