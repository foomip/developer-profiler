package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.search.Finder

/**
 * Created by nelsonpascoal on 2015/08/02.
 */
class FinderModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = bindActor[Finder]("SearchFinder")
}
