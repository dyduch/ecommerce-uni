package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplierRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, addressRepository: AddressRepository) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class SupplierTable(tag: Tag) extends Table[Supplier](tag, "supplier") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def address = column[Long]("address")

    def address_fk = foreignKey("address_fk", address, address_table)(_.id)

    override def * = (id, name, address) <> ((Supplier.apply _).tupled, Supplier.unapply)

  }

  import addressRepository.AddressTable

  private val address_table = TableQuery[AddressTable]
  private val supplier_table = TableQuery[SupplierTable]

  def create(name: String, address: Long): Future[Supplier] = db.run {
    (supplier_table.map(supplier => (supplier.name, supplier.address))
      returning supplier_table.map(_.id)
      into { case ((name, address), id) => Supplier(id, name, address) }
      ) += (name, address)
  }

  def list(): Future[Seq[Supplier]] = db.run {
    supplier_table.result
  }

  def getByAddress(address_id: Long): Future[Seq[Supplier]] = db.run {
    supplier_table.filter(_.address === address_id).result
  }

  def getById(id: Long): Future[Supplier] = db.run {
    supplier_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Supplier]] = db.run {
    supplier_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(supplier_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_supplier: Supplier): Future[Unit] = {
    val supplierToUpdate: Supplier = new_supplier.copy(id)
    db.run(supplier_table.filter(_.id === id).update(supplierToUpdate)).map(_ => ())
  }
}