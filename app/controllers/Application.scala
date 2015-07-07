package controllers

import com.google.inject.Inject
import daos.BackgroundImageDAO
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject() (backgroundImageDAO: BackgroundImageDAO) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }


  def backgroundDetails(page: String) = Action.async { implicit request =>
    backgroundImageDAO.findByPage(page).map {
      case Some(bi) => Ok(views.html.partials.background_details(bi))
      case _ => NotFound("Invalid page value")
    }
  }
}
