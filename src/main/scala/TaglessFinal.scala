import TaglessFinalDI._
import cats.Monad
import cats.effect.IO
import cats.implicits._

object TaglessFinalDI {

  case class Course(code: String, name: String)

  trait AuthAlgebra[F[_]] {
    def isAuthorised(userName: String): F[Boolean]
  }

  object AuthAlgebra {
    def apply[F[_]: AuthAlgebra]: AuthAlgebra[F] = implicitly
  }

  class AuthInterpreter[F[_]: Monad] extends AuthAlgebra[F] {
    def isAuthorised(userName: String): F[Boolean] = Monad[F].pure(userName.startsWith("J"))
  }

  trait CourseAlgebra[F[_]] {
    def register(course: Course, userName: String, isAuthorised: Boolean): F[String]
  }

  object CourseAlgebra {
    def apply[F[_]: CourseAlgebra]: CourseAlgebra[F] = implicitly
  }

  //Individual service / Dependencies
  class CourseInterpreter[F[_]: Monad] extends CourseAlgebra[F] {
    def register(course: Course, userName: String, isAuthorised: Boolean) = {
      Monad[F].pure(
        if (isAuthorised)
          s"User $userName registered for the course: ${course.code}"
        else
          s"User: $userName is not authorised to register for course: ${course.code}"
      )
    }
  }

  //DI using implicits
  class RegistrationProgram[F[_]: Monad: AuthAlgebra: CourseAlgebra]() {

    def registerWhenAuthorised(course: Course, userName: String): F[String] = for {
      authorised <- AuthAlgebra[F].isAuthorised(userName)
      result <- CourseAlgebra[F].register(course, userName, authorised)
    } yield result
  }
}

object TaglessFinalApp extends App {

  val course = Course("Computer Science", "CS")

  implicit val authService = new AuthInterpreter[IO]
  implicit val courseService = new CourseInterpreter[IO]

  val courseManager = new RegistrationProgram

  val register1 = courseManager.registerWhenAuthorised(course, "Jack")
  val register2 = courseManager.registerWhenAuthorised(course, "Sameer")

  println(register1.unsafeRunSync())
  println(register2.unsafeRunSync())
}

/*
Test implementation
object TaglessFinalTest {
  implicit val authService = new AuthInterpreter[Id]
  implicit val courseService = new CourseInterpreter[Id]

  val courseManager = new RegistrationProgram(authService, courseService)
  private val result: String = courseManager.registerWhenAuthorised(Course("Computer Science", "CS"), "")
  assert(result == "")
}*/
