package daos

import models.PageWord
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * Created by nelsonpascoal on 2015/07/30.
 */
class PageWordDAO(dbName: String = "default") extends HasDatabaseConfig[JdbcProfile] {
  import driver.api._

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](dbName)(Play.current)

  private val PageWords = TableQuery[PageWordsTable]

  private class PageWordsTable(tag: Tag) extends Table[PageWord](tag, "PageWords") {
    def id                = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def indexablePageId   = column[Long]("indexablePageId")
    def word              = column[String]("word")

    def * = (id.?, indexablePageId, word) <> (PageWord.tupled, PageWord.unapply)
  }

  private def queryByPage(id: Long) = PageWords filter(_.indexablePageId === id)

  def create(p: PageWord): Future[PageWord] = db.run {
    (PageWords returning PageWords.map(_.id)
      into ((p, id) => p.copy(id = Some(id)))
    ) += p
  }

  def removeForPage(pageId: Long) = db.run {
    queryByPage(pageId).delete
  }
}
