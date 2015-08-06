package daos

import models.PageWord
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import scala.language.postfixOps

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

  private def queryWordMatchStats(words: List[String]) = {
    val totals = PageWords.groupBy(_.indexablePageId).map {
      case (pid, w) => (pid, w.size)
    }
    val hits = PageWords.filter(_.word inSet words).groupBy(_.indexablePageId).map {
      case (pid, w) => (pid, w.size)
    }

    totals joinLeft hits on {
      case ((pid1, _),(pid2, _)) => pid1 === pid2
    }
  }

  def removeForPage(pageId: Long) = db.run {
    queryByPage(pageId).delete
  }

  def wordMatches(words: List[String]) = db.run { (queryWordMatchStats(words) filter(_._2.isDefined)).result } map ( _ map {
    case (x, Some(y)) =>
      val (pid, total)  = x
      val (_, hits)     = y

      (pid, total, hits, hits.toFloat / total)
  } sortBy(_._4) reverse)
}
