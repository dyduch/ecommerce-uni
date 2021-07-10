package models

import play.api.libs.json.{Json, OFormat}

case class ProductQuantity(id: Long, size: Int, quantity: Int,  product_id: Long)

object ProductQuantity {
  implicit val ProductQuantityFormat: OFormat[ProductQuantity] = Json.format[ProductQuantity]
}