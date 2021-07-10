package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def password = column[String]("password")

    def admin = column[Boolean]("admin")

    def address_id = column[Long]("address_id")

    override def * = (id, name, password, admin, address_id) <> ((User.apply _).tupled, User.unapply)

  }

  private val user_table = TableQuery[UserTable]

  def create(name: String, password: String, admin: Boolean, address_id: Long): Future[User] = db.run {
    (user_table.map(user => (user.name, user.password, user.admin, user.address_id))
      returning user_table.map(_.id)
      into { case ((name, password, admin, address_id), id) => User(id, name, password, admin, address_id) }
      ) += (name, password, admin, address_id)
  }

  def list(): Future[Seq[User]] = db.run {
    user_table.result
  }

  def listOfAdmins(): Future[Seq[User]] = db.run {
    user_table.filter(_.admin === true).result
  }

  def getByAddress(address_id: Long): Future[Seq[User]] = db.run {
    user_table.filter(_.address_id === address_id).result
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