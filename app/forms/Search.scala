package forms

import play.api.data._
import play.api.data.Forms._

/**
 * Created by nelsonpascoal on 2015/08/01.
 */
object Search {
  case class Search(searchString: String)

  val searchForm = Form(
    mapping(
      "searchString" -> nonEmptyText
    )(Search.apply)(Search.unapply)
  )
}

