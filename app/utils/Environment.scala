package utils

import play.api.Play

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
trait Environment {
  lazy val environment = {
    if(Play.isProd(Play.current)) "production"
    else if(Play.isTest(Play.current)) "test"
    else "development"
  }
}
