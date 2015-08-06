package modules

import akka.actor.ActorSystem
import com.google.inject.{Singleton, AbstractModule}
import play.api.Logger
import play.api.libs.concurrent.Akka
import services.search.Supervisor
import utils.WaitForPlay
import sys.process._
import scala.language.postfixOps

/**
 * Created by nelsonpascoal on 2015/07/13.
 */

object SearchIndexer {
  val system = ActorSystem(s"SearchIndex")
}

@Singleton
class SearchIndexer extends AbstractModule with WaitForPlay {
  import services.search.Supervisor.Run

  val system = SearchIndexer.system

  def configure() = {
    val result = { "which phantomjs" ! }

    if (result != 0) {
      println("Server needs to have PhantomJS executable installed, cannot start Search ReIndexer")
    }
    else {
      println("Search Indexer - waiting for Play application to start")

      waitForPlay {
        Logger.info("Play started - spinning up Search Indexer")

        import play.api.Play.current

        val searchSystem = Akka.system.actorOf(Supervisor.props, "SearchSupervisor")

        system.shutdown()
        searchSystem ! Run
      }
    }
  }
}
