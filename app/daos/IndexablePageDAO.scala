package daos

import models.IndexablePage
import play.api.Play
import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
class IndexablePageDAO(dbName: String = "default") extends HasDatabaseConfig[JdbcProfile] {
  import driver.api._

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](dbName)(Play.current)

  private val IndexablePages = TableQuery[IndexablePagesTable]

  private class IndexablePagesTable(tag: Tag) extends Table[IndexablePage](tag, "IndexablePages") {
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def description = column[String]("description")
    def path        = column[String]("path")
    def active      = column[Boolean]("active")

    def * = (id.?, description, path, active) <> (IndexablePage.tupled, IndexablePage.unapply)
  }

  private def queryAll = for {
    p <- IndexablePages if p.active
  } yield p

  def findAll = db.run { queryAll.result }
}
