package models

import play.api.libs.json.{Json, OFormat}

case class Image(id: Long, url: String, product_id: Long)

object Image {
  implicit val imageFormat: OFormat[Image] = Json.format[Image]
}