package models

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
case class IndexablePage(id: Option[Long] = None, description: String, path: String, active: Boolean = true)
