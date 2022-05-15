package repositories

import models.Book

import javax.inject.Singleton
import scala.collection.mutable

@Singleton
class BookRepository {

  private val bookList = mutable.Set[Book]()
  bookList += Book(1,
    "Programming in Scala, Fifth Edition",
    "Martin Odersky",
    "Scala programming language",
    "Development"
  )

  def getAllBooks: mutable.Set[Book] = bookList

  /**
   * @param bookId The ID of the book to find
   * @return Option[Book] An option means this method can return a Book or optionally a "None"
   *         https://www.scala-lang.org/api/2.13.8/scala/Option.html
   */
  def getBook(bookId: Long): Option[Book] = bookList.collectFirst {
    case book if book.id == bookId => book
  }

  @throws(classOf[Exception])
  def addBook(book: Book): Option[Book] = {
    // If book already exists then return none
    if(bookList.exists(b => b.id == book.id)) {
      throw new Exception("Book id already exist")
    }
    else {
      // otherwise return the saved after adding it
      bookList.addOne(book).collectFirst {
        case b if b.id == book.id => b
      }
    }
  }

  @throws(classOf[Exception])
  def deleteBook(bookId: Long): Unit = {
    if(!bookList.exists(_.id == bookId)) {
      throw new Exception("Book not found")
    }
    bookList --= bookList.filter( _.id == bookId)
  }

}
