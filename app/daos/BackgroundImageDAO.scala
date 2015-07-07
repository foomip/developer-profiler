package daos

import com.google.inject.Inject
import models.BackgroundImage
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * Created by nelsonpascoal on 2015/07/06.
 */
class BackgroundImageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val BackgroundImages = TableQuery[BackgroundImagesTable]

  private class BackgroundImagesTable(tag: Tag) extends Table[BackgroundImage](tag, "BackgroundImages") {
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def page        = column[String]("page")
    def imagePath   = column[String]("image_path")
    def title       = column[String]("title")
    def description = column[String]("description")

    def * = (id.?, page, title, imagePath, description) <> (BackgroundImage.tupled, BackgroundImage.unapply)
  }

  def findByPage(page: String): Future[Option[BackgroundImage]] = db.run {
    BackgroundImages.filter(_.page === page).result.map(_.headOption)
  }
}
