package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, addressRepository: AddressRepository) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def password = column[String]("password")

    def admin = column[Boolean]("admin")

    def address = column[Long]("address")

    def address_fk = foreignKey("address_fk", address, address_table)(_.id)

    override def * = (id, name, password, admin, address) <> ((User.apply _).tupled, User.unapply)

  }

  import addressRepository.AddressTable

  private val address_table = TableQuery[AddressTable]
  private val user_table = TableQuery[UserTable]

  def create(name: String, password: String, admin: Boolean, address: Long): Future[User] = db.run {
    (user_table.map(user => (user.name, user.password, user.admin, user.address))
      returning user_table.map(_.id)
      into { case ((name, password, admin, address), id) => User(id, name, password, admin, address) }
      ) += (name, password, admin, address)
  }

  def list(): Future[Seq[User]] = db.run {
    user_table.result
  }

  def listOfAdmins(): Future[Seq[User]] = db.run {
    user_table.filter(_.admin === true).result
  }

  def getByAddress(address_id: Long): Future[Seq[User]] = db.run {
    user_table.filter(_.address === address_id).result
  }

  def getById(id: Long): Future[User] = db.run {
    user_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[User]] = db.run {
    user_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(user_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_user: User): Future[Unit] = {
    val userToUpdate: User = new_user.copy(id)
    db.run(user_table.filter(_.id === id).update(userToUpdate)).map(_ => ())
  }
}