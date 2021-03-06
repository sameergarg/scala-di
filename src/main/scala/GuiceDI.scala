import GuiceDI.{Course, CourseManager}
import com.google.inject.{AbstractModule, Guice, Inject}
import net.codingwell.scalaguice.ScalaModule

/**
  * Register authorised users to a course
  */
object GuiceDI {

  case class Course(code: String, name: String)

  //Individual service / Dependencies

  /**
    * Checks if user is registered
    */
  class AuthService {
    def isAuthorised(userName: String): Boolean = userName.startsWith("J")
  }

  /**
    * Register user in a course if authorised
    */
  class CourseService {
    def register(course: Course, userName: String, isAuthorised: Boolean) = {
      if (isAuthorised)
        s"User $userName registered for the course: ${course.code}"
      else
        s"User: $userName is not authorised to register for course: ${course.code}"
    }
  }

  //DI
  /**
    * Facade with collaborators
    * @param authService
    * @param courseService
    */
  class CourseManager @Inject()(authService: AuthService, courseService: CourseService) {

    def registerWhenAuthorised(course: Course, userName: String): String = {

      val authorised = authService.isAuthorised(userName)
      courseService.register(course, userName, authorised)

    }
  }

  //Guice Module - like component registry
  class MyModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
      bind[AuthService]
      bind[CourseService]
      bind[CourseManager]
    }
  }
}

object GuiceMain extends App {
  import net.codingwell.scalaguice.InjectorExtensions._

  val injector = Guice.createInjector()
  val service = injector.instance[CourseManager]

  val course = Course("Computer Science", "CS")
  val register1 = service.registerWhenAuthorised(course, "Jack")
  val register2 = service.registerWhenAuthorised(course, "Sam")

  println(register1)
  println(register2)
}