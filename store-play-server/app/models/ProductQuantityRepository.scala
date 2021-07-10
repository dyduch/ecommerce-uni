package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductQuantityRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ProductQuantityTable(tag: Tag) extends Table[ProductQuantity](tag, "product_quantity") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def quantity = column[Int]("quantity")

    def size = column[Int]("size")

    def product = column[Long]("product")

    def product_fk = foreignKey("product_fk", product, product_table)(_.id)

    override def * = (id, quantity, size, product) <> ((ProductQuantity.apply _).tupled, ProductQuantity.unapply)
  }

  import productRepository.ProductTable

  private val product_table = TableQuery[ProductTable]
  private val pq_table = TableQuery[ProductQuantityTable]

  def create(quantity: Int, size: Int, product: Long): Future[ProductQuantity] = db.run {
    (pq_table.map(pq => (pq.quantity, pq.size, pq.product))
      returning pq_table.map(_.id)
      into { case ((quantity, size, product), id) => ProductQuantity(id, quantity, size, product) }
      ) += (quantity, size, product)
  }

  def list(): Future[Seq[ProductQuantity]] = db.run {
    pq_table.result
  }

  def getByProduct(product_id: Long): Future[Seq[ProductQuantity]] = db.run {
    pq_table.filter(_.product === product_id).result
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