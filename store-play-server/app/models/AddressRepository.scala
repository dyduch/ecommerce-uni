package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class AddressTable(tag: Tag) extends Table[Address](tag, "address") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def street = column[String]("street")

    def number = column[String]("number")

    def city = column[String]("city")

    def zipcode = column[String]("zipcode")

    def country = column[String]("country")

    def * = (id, street, number, city, zipcode, country) <> ((Address.apply _).tupled, Address.unapply)
  }

  val address_table = TableQuery[AddressTable]

  def create(street: String, number: String, city: String, zipcode: String, country: String): Future[Address] = db.run {
    (address_table.map(a => (a.street, a.number, a.city, a.zipcode, a.country))
      returning address_table.map(_.id)
      into { case ((street, number, city, zipcode, country), id) => Address(id, street, number, city, zipcode, country) }
      ) += (street, number, city, zipcode, country)
  }

  def list(): Future[Seq[Address]] = db.run {
    address_table.result
  }

  def delete(id: Long): Future[Unit] = db.run(address_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_address: Address): Future[Unit] = {
    val addressToUpdate: Address = new_address.copy(id)
    db.run(address_table.filter(_.id === id).update(addressToUpdate)).map(_ => ())
  }
}