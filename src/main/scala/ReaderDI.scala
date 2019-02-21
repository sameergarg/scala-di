import ReaderDI._
import cats.data.Reader

object ReaderDI {

  case class Course(code: String, name: String)

  //Individual service / Dependencies
  class AuthService {
    def isAuthorised(userName: String): Boolean = userName.startsWith("J")
  }

  //Individual service / Dependencies
  class CourseService {
    def register(course: Course, userName: String, isAuthorised: Boolean) = {
      if (isAuthorised)
        s"User $userName registered for the course: ${course.code}"
      else
        s"User: $userName is not authorised to register for course: ${course.code}"
    }
  }

  //a.k.a Application Context
  case class Environment( authService: AuthService,
                          courseService: CourseService)



  //Algebra
  class CourseManagerReaderOps {
    def isAuthorised(userName: String) = Reader[Environment, Boolean]{ courseMgr =>
      courseMgr.authService.isAuthorised(userName)
    }

    def register(course: Course, userName: String, isFull: Boolean) = Reader[Environment, String] { courseMgr =>
      courseMgr.courseService.register(course, userName,  isFull)
    }
  }

  //DI using reader - Program
  class CourseManager extends CourseManagerReaderOps {
    def registerWhenAuthorised(course: Course,
                              userName: String): Reader[Environment, String] = {
      for {
        authorised <- isAuthorised(userName)
        response   <- register(course, userName, authorised)
      } yield response
    }
  }
}

object Main extends App {
  val course = Course("Computer Science", "CS")
  val env = new Environment(new AuthService, new CourseService)

  private val manager = new CourseManager()

  val register1 = manager.registerWhenAuthorised(course, "Jack")
  val register2 = manager.registerWhenAuthorised(course, "Sam")

  println(register1.run(env))
  println(register2.run(env))
}
