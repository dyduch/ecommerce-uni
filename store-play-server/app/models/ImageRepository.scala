package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImageRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ImageTable(tag: Tag) extends Table[Image](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def url = column[String]("url")

    def product = column[Long]("product")

    def product_fk = foreignKey("product_fk", product, product_table)(_.id)

    override def * = (id, url, product) <> ((Image.apply _).tupled, Image.unapply)

  }

  import productRepository.ProductTable

  private val product_table = TableQuery[ProductTable]
  private val image_table = TableQuery[ImageTable]

  def create(url: String, product: Long): Future[Image] = db.run {
    (image_table.map(image => (image.url, image.product))
      returning image_table.map(_.id)
      into { case ((url, product), id) => Image(id, url, product) }
      ) += (url, product)
  }

  def list(): Future[Seq[Image]] = db.run {
    image_table.result
  }

  def getByProduct(product_id: Long): Future[Seq[Image]] = db.run {
    image_table.filter(_.product === product_id).result
  }

  def getById(id: Long): Future[Image] = db.run {
    image_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Image]] = db.run {
    image_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(image_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_image: Image): Future[Unit] = {
    val imageToUpdate: Image = new_image.copy(id)
    db.run(image_table.filter(_.id === id).update(imageToUpdate)).map(_ => ())
  }
}