package controllers

import com.google.inject.Inject
import daos.BackgroundImageDAO
import org.joda.time.{Years, DateTime}
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject() (backgroundImageDAO: BackgroundImageDAO) extends Controller {

  lazy val startDate = new DateTime(2005, 2, 1, 0, 0)
  lazy val frontendStartDate = new DateTime(2013, 6, 1, 0, 0)

  def index = Action {
    Ok(views.html.index())
  }

  def aboutMe = Action {
    val endDate = DateTime.now()
    val numberOfYears = Years.yearsBetween(startDate, endDate).getYears
    val numberFrontEndYears = Years.yearsBetween(frontendStartDate, endDate).getYears

    Ok(views.html.about_me(numberOfYears, numberFrontEndYears))
  }

  def aboutSite = Action {
    Ok(views.html.about_site())
  }

  def backgroundDetails(page: String) = Action.async { implicit request =>
    backgroundImageDAO.findByPage(page).map {
      case Some(bi) => Ok(views.html.partials.background_details(bi))
      case _ => NotFound("Invalid page value")
    }
  }
}
