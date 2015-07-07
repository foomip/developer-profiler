package models

/**
 * Created by nelsonpascoal on 2015/07/06.
 */
case class BackgroundImage(
  id:           Option[Long] = None,
  page:         String,
  title:        String,
  imagePath:    String,
  description:  String
)
