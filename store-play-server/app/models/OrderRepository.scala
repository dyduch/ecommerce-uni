package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, addressRepository: AddressRepository, userRepository: UserRepository) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def total = column[Double]("total")

    def date = column[String]("date")

    def user = column[Long]("user")

    def user_fk = foreignKey("user_fk", user, user_table)(_.id)

    def address = column[Long]("address")

    def address_fk = foreignKey("address_fk", address, address_table)(_.id)

    override def * = (id, date, total, user, address) <> ((Order.apply _).tupled, Order.unapply)

  }
  import addressRepository.AddressTable
  import userRepository.UserTable

  private val address_table = TableQuery[AddressTable]
  private val user_table = TableQuery[UserTable]
  private val order_table = TableQuery[OrderTable]

  def create(date: String, total: Double, user: Long, address: Long): Future[Order] = db.run {
    (order_table.map(order => (order.date, order.total, order.user, order.address))
      returning order_table.map(_.id)
      into { case ((date, total, user, address), id) => Order(id, date, total, user, address) }
      ) += (date, total, user, address)
  }

  def list(): Future[Seq[Order]] = db.run {
    order_table.result
  }

  def getByAddress(address_id: Long): Future[Seq[Order]] = db.run {
    order_table.filter(_.address === address_id).result
  }

  def getById(id: Long): Future[Order] = db.run {
    order_table.filter(_.id === id).result.head
  }

  def getByUser(user_id: Long): Future[Seq[Order]] = db.run {
    order_table.filter(_.user === user_id).result
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