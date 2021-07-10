package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, date: String, total: Int, user_id: Long, address_id: Long)

object Order {
  implicit val OrderFormat: OFormat[Order] = Json.format[Order]
}