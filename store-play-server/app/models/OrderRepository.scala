package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def total = column[Double]("total")

    def date = column[String]("date")

    def user_id = column[Long]("user_id")

    def address_id = column[Long]("address_id")

    override def * = (id, date, total, user_id, address_id) <> ((Order.apply _).tupled, Order.unapply)

  }

  private val order_table = TableQuery[OrderTable]

  def create(date: String, total: Double, user_id: Long, address_id: Long): Future[Order] = db.run {
    (order_table.map(order => (order.date, order.total, order.user_id, order.address_id))
      returning order_table.map(_.id)
      into { case ((date, total, user_id, address_id), id) => Order(id, date, total, user_id, address_id) }
      ) += (date, total, user_id, address_id)
  }

  def list(): Future[Seq[Order]] = db.run {
    order_table.result
  }

  def getByAddress(address_id: Long): Future[Seq[Order]] = db.run {
    order_table.filter(_.address_id === address_id).result
  }

  def getById(id: Long): Future[Order] = db.run {
    order_table.filter(_.id === id).result.head
  }

  def getByUser(user_id: Long): Future[Seq[Order]] = db.run {
    order_table.filter(_.user_id === user_id).result
  }

  def getByIdOption(id: Long): Future[Option[Order]] = db.run {
    order_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(order_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_order: Order): Future[Unit] = {
    val orderToUpdate: Order = new_order.copy(id)
    db.run(order_table.filter(_.id === id).update(orderToUpdate)).map(_ => ())
  }
}