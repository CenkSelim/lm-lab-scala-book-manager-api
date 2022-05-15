package errorhandlers

import akka.http.scaladsl.model.StatusCodes
import models.Book
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import scala.language.postfixOps


class ErrorHandlerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with Eventually {
  "ErrorHandler" should {
    "return 404 when a book has not been found" in {
      //given
      val objectUnderTest = new ErrorHandler()
      val request = FakeRequest(GET, "/books/99")

      //when
      val responseFuture = objectUnderTest.onServerError(request,new Exception("Book not found"))

      //then
      eventually {
        status(responseFuture) mustBe 404
      }
    }

    "return 500 when a book Id already exists when adding" in {
      //given
      var sampleBook: Option[Book] = Option(
        Book(1, "The classic novel", "Anon", "Brilliant", "pseudo fiction")
      )
      val objectUnderTest = new ErrorHandler()
      val request =  FakeRequest(POST, "/books").withJsonBody(Json.toJson(sampleBook))

      //when
      val responseFuture = objectUnderTest.onServerError(request,new Exception("Book id already exist"))

      //then
      eventually {
        status(responseFuture) mustBe 500
      }
    }

  }
}
