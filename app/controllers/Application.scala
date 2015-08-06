package controllers

import akka.pattern.ask
import akka.actor.ActorRef
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Singleton, Inject}
import daos.BackgroundImageDAO
import org.joda.time.{Years, DateTime}
import play.api.mvc._
import scala.language.postfixOps
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

@Singleton
class Application @Inject() (
  backgroundImageDAO: BackgroundImageDAO,
  @Named("SearchFinder") finder: ActorRef)
  (implicit ec: ExecutionContext) extends Controller {

  implicit val timeout =  Timeout(30 seconds)

  import forms.Search._
  import services.search.Finder.Search

  val startDate = new DateTime(2005, 2, 1, 0, 0)
  val frontendStartDate = new DateTime(2013, 6, 1, 0, 0)

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

  def search = Action.async { implicit request =>
    val searchData = searchForm.bindFromRequest.get

    finder ? Search(text = searchData.searchString) map { searchData =>
      println(searchData)
      Ok
    } recover {
      case e: Throwable =>
        println(e.getMessage)
        InternalServerError("Error")
    }
  }

  def backgroundDetails(page: String) = Action.async { implicit request =>
    backgroundImageDAO.findByPage(page).map {
      case Some(bi) => Ok(views.html.partials.background_details(bi))
      case _ => NotFound("Invalid page value")
    }
  }
}
