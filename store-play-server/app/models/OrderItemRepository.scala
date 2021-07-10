package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderItemRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "order_item") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def quantity = column[Int]("quantity")

    def size = column[Int]("size")

    def product_id = column[Long]("product_id")

    def order_id = column[Long]("order_id")

    override def * = (id, size, quantity, product_id, order_id) <> ((OrderItem.apply _).tupled, OrderItem.unapply)

  }

  private val oi_table = TableQuery[OrderItemTable]

  def create(size: Int, quantity: Int, order_id: Long, product_id: Long): Future[OrderItem] = db.run {
    (oi_table.map(orderItem => (orderItem.size, orderItem.quantity, orderItem.order_id, orderItem.product_id))
      returning oi_table.map(_.id)
      into { case ((size, quantity, order_id, product_id), id) => OrderItem(id, size, quantity, order_id, product_id) }
      ) += (size, quantity, order_id, product_id)
  }

  def list(): Future[Seq[OrderItem]] = db.run {
    oi_table.result
  }

  def getByOrder(order_id: Long): Future[Seq[OrderItem]] = db.run {
    oi_table.filter(_.order_id === order_id).result
  }

  def getById(id: Long): Future[OrderItem] = db.run {
    oi_table.filter(_.id === id).result.head
  }

  def getByProduct(product_id: Long): Future[Seq[OrderItem]] = db.run {
    oi_table.filter(_.product_id === product_id).result
  }

  def getByIdOption(id: Long): Future[Option[OrderItem]] = db.run {
    oi_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(oi_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_orderItem: OrderItem): Future[Unit] = {
    val orderItemToUpdate: OrderItem = new_orderItem.copy(id)
    db.run(oi_table.filter(_.id === id).update(orderItemToUpdate)).map(_ => ())
  }
}