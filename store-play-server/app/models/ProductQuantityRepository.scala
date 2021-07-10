package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductQuantityRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ProductQuantityTable(tag: Tag) extends Table[ProductQuantity](tag, "product_quantity") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def quantity = column[Int]("quantity")

    def size = column[Int]("size")

    def product_id = column[Long]("product_id")

    override def * = (id, quantity, size, product_id) <> ((ProductQuantity.apply _).tupled, ProductQuantity.unapply)
  }

  private val pq_table = TableQuery[ProductQuantityTable]

  def create(quantity: Int, size: Int, product_id: Long): Future[ProductQuantity] = db.run {
    (pq_table.map(pq => (pq.quantity, pq.size, pq.product_id))
      returning pq_table.map(_.id)
      into { case ((quantity, size, product_id), id) => ProductQuantity(id, quantity, size, product_id) }
      ) += (quantity, size, product_id)
  }

  def list(): Future[Seq[ProductQuantity]] = db.run {
    pq_table.result
  }

  def getByProduct(product_id: Long): Future[Seq[ProductQuantity]] = db.run {
    pq_table.filter(_.product_id === product_id).result
  }

  def getById(id: Long): Future[ProductQuantity] = db.run {
    pq_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[ProductQuantity]] = db.run {
    pq_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(pq_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_pq: ProductQuantity): Future[Unit] = {
    val pqToUpdate: ProductQuantity = new_pq.copy(id)
    db.run(pq_table.filter(_.id === id).update(pqToUpdate)).map(_ => ())
  }
}