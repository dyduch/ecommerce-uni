package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderStatusRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class OrderStatusTable(tag: Tag) extends Table[OrderStatus](tag, "order_status") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def status = column[String]("status")

    def order = column[Long]("order")

    def order_fk = foreignKey("order_fk", order, order_table)(_.id)

    override def * = (id, status, order) <> ((OrderStatus.apply _).tupled, OrderStatus.unapply)

  }

  import orderRepository.OrderTable

  private val order_table = TableQuery[OrderTable]
  private val os_table = TableQuery[OrderStatusTable]

  def create(status: String, order: Long): Future[OrderStatus] = db.run {
    (os_table.map(os => (os.status, os.order))
      returning os_table.map(_.id)
      into { case ((status, order), id) => OrderStatus(id, status, order) }
      ) += (status, order)
  }

  def list(): Future[Seq[OrderStatus]] = db.run {
    os_table.result
  }

  def getByOrder(order_id: Long): Future[Seq[OrderStatus]] = db.run {
    os_table.filter(_.order === order_id).result
  }

  def getById(id: Long): Future[OrderStatus] = db.run {
    os_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[OrderStatus]] = db.run {
    os_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(os_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_os: OrderStatus): Future[Unit] = {
    val osToUpdate: OrderStatus = new_os.copy(id)
    db.run(os_table.filter(_.id === id).update(osToUpdate)).map(_ => ())
  }
}