package daos

import models.IndexablePage
import play.api.Play
import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by nelsonpascoal on 2015/07/26.
 */
class IndexablePageDAO(dbName: String = "default") extends HasDatabaseConfig[JdbcProfile] {
  import driver.api._

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](dbName)(Play.current)

  private val IndexablePages = TableQuery[IndexablePagesTable]

  private class IndexablePagesTable(tag: Tag) extends Table[IndexablePage](tag, "IndexablePages") {
    def id                  = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def description         = column[String]("description")
    def path                = column[String]("path")
    def active              = column[Boolean]("active")
    def indexedTitle        = column[Option[String]]("indexedTitle")
    def indexedDescription  = column[Option[String]]("indexedDescription")

    def * = (id.?, description, path, active, indexedTitle, indexedDescription) <> (IndexablePage.tupled, IndexablePage.unapply)
  }

  private def queryAll = for {
    p <- IndexablePages if p.active
  } yield p

  private def queryById(id: Long) = for {
    p <- IndexablePages if p.id === id
  } yield p

  def findAll = db.run { queryAll.result }

  def findById(id: Long) = db.run { queryById(id).result.headOption }

  def updateSearchDescriptions(id: Long, title: String, descr: String) = {
    val description = if(descr.length > 50) s"${descr.substring(0, 47)}..." else descr

    findById(id) flatMap {
      case Some(i)  => {
        val update = i.copy(indexedTitle = Some(title), indexedDescription = Some(description))

        db.run { queryById(id).update(update) } map { x =>
          if(x == 1) Some(update) else None
        }
      }
      case _        => Future successful None
    }
  }
}
