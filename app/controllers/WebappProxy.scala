package controllers

import javax.inject.Inject

import play.api.libs.iteratee.Enumerator
import play.api.libs.ws.WSClient
import play.api.mvc.{ResponseHeader, Result, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by nelsonpascoal on 2015/06/22.
 */
class WebappProxy @Inject() (ws: WSClient) extends Controller {

  def request(path: String) = Action.async { request =>
    val url = s"http://127.0.0.1:4200/webapp/$path"

    ws.url(url).get().map { r =>
      val headers = r.allHeaders.keys.foldLeft(Map[String, String]()) { (headers, key) =>
        r.header(key) match {
          case Some(v)  => headers + (key -> v)
          case _        => headers
        }
      }

      new Result(
        header = ResponseHeader(status = r.status, headers = headers),
        body = Enumerator(r.bodyAsBytes)
      )
    }
  }
}
