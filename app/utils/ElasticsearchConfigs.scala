package utils

import play.api.Play
import play.api.Play.current

/**
 * Created by nelsonpascoal on 2015/08/02.
 */
trait ElasticsearchConfigs { this: Environment =>
  lazy val elasticSearchIndex = Play.configuration.getString("elasticsearch.searchIndex").getOrElse("developer_profiler")
  lazy val elasticSearchHost  = Play.configuration.getString(s"elasticsearch.$environment.url").getOrElse("http://127.0.0.1:9200")
  lazy val requestUri         = s"$elasticSearchHost/$elasticSearchIndex/_analyze?text="
}
