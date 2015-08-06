package models

/**
 * Created by nelsonpascoal on 2015/08/05.
 */
case class SearchStats(
  indexablePageId: Long,
  totalWords: Long,
  hits: Long,
  score: Float,
  pageTitle: String,
  pageDescription: String
)
