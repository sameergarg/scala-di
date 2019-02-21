import GuiceDI.{AuthService, Course, CourseManager, CourseService}

object ConstructorDI {

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

  //DI
  class CourseManager(authService: AuthService, courseService: CourseService) {

    def registerWhenAuthorised(course: Course, userName: String): String = {

      val authorised = authService.isAuthorised(userName)
      courseService.register(course, userName, authorised)

    }
  }
}

object ConstructorMain extends App {
  val course = Course("Computer Science", "CS")

  val authService = new AuthService
  val courseService = new CourseService

  val courseManager = new CourseManager(authService, courseService)

  val register1 = courseManager.registerWhenAuthorised(course, "Jack")
  val register2 = courseManager.registerWhenAuthorised(course, "Sam")

  println(register1)
  println(register2)
}