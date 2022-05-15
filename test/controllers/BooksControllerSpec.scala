package controllers

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import repositories.BookRepository
import models.Book
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.when
import play.api.libs.json._

import scala.collection.mutable

class BooksControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with MockitoSugar {

  val mockDataService: BookRepository = mock[BookRepository]
  var sampleBook: Option[Book] = Option(
    Book(2, "The classic novel", "Anon", "Brilliant", "pseudo fiction")
  )
  var sampleBook3: Option[Book] = Option(
    Book(3, "The classic novel III", "Anon", "Brilliant", "pseudo fiction")
  )
  var duplicateSampleBook: Option[Book] = Option(
    Book(1,"Programming in Scala, Fifth Edition","Martin Odersky","Scala programming language","Development")
  )

  "BooksController GET allBooks" should {

    "return 200 OK for all books request" in {

      // Here we utilise Mockito for stubbing the request to getAllBooks
      when(mockDataService.getAllBooks).thenReturn(mutable.Set[Book]())

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)
      val allBooks = controller.getAll().apply(FakeRequest(GET, "/books"))

      status(allBooks) mustBe OK
      contentType(allBooks) mustBe Some("application/json")
    }

    "return empty JSON array of books for all books request" in {

      // Here we utilise Mockito for stubbing the request to getAllBooks
      when(mockDataService.getAllBooks) thenReturn mutable.Set[Book]()

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)
      val allBooks = controller.getAll().apply(FakeRequest(GET, "/books"))

      status(allBooks) mustBe OK
      contentType(allBooks) mustBe Some("application/json")
      contentAsString(allBooks) mustEqual "[]"
    }
  }

  "BooksController GET bookById" should {

    "return 200 OK for single book request" in {

      // Here we utilise Mockito for stubbing the request to getBook
      when(mockDataService.getBook(1)) thenReturn sampleBook

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)
      val book = controller.getBook(1).apply(FakeRequest(GET, "/books/1"))

      status(book) mustBe OK
      contentType(book) mustBe Some("application/json")
    }

    "return Book not found for unknown single book request" in {

      // Here we utilise Mockito for stubbing the request to getBook
      when(mockDataService.getBook(99)) thenReturn None

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)

      val thrown = intercept[Exception] {
        val book = controller.getBook(99).apply(FakeRequest(GET, "/books/99"))
      }
      assert(thrown.getMessage === "Book not found")
    }

  }

  "BooksController Delete Book" should {

    "return 200 OK for deleting a single book" in {

      // Here we utilise Mockito for stubbing the request to addBook
      when(mockDataService.addBook(any())) thenReturn sampleBook3

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)
      val book = controller
        .addBook()
        .apply(
          FakeRequest(POST, "/books").withJsonBody(Json.toJson(sampleBook3))
        )

      val bookDelete =
        controller.deleteBook(3).apply(FakeRequest(DELETE, "/books/3"))

      status(bookDelete) mustBe OK
    }

    "throw an error when deleting a book that doesn't exist" in {
      when(mockDataService.deleteBook(anyLong())) thenThrow new Exception("Book not found")
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val exceptionCaught = intercept[Exception] {
        controller.deleteBook(99).apply(FakeRequest(DELETE, "/books/99"))
      }

      exceptionCaught.getMessage mustBe "Book not found"
    }
  }

  "BooksController POST addBook" should {

    "return 200 OK for adding a single book" in {

      // Here we utilise Mockito for stubbing the request to addBook
      when(mockDataService.addBook(any())) thenReturn sampleBook

      val controller =
        new BooksController(stubControllerComponents(), mockDataService)
      val book = controller
        .addBook()
        .apply(
          FakeRequest(POST, "/books").withJsonBody(Json.toJson(sampleBook))
        )

      status(book) mustBe CREATED
      contentType(book) mustBe Some("application/json")
    }

    "throw an error when adding and a book id that already exist" in {
      when(mockDataService.addBook(any())) thenThrow new Exception("Book id already exist")
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val exceptionCaught = intercept[Exception] {
        controller.addBook().apply(FakeRequest(POST, "/books").withJsonBody(Json.toJson(duplicateSampleBook)))
      }

      exceptionCaught.getMessage mustBe "Book id already exist"
    }

  }



}
