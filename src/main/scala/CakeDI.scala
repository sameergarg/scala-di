import CakeDI.{AuthServiceComponentImpl, Course, CourseManagerComponentImpl, CourseServiceComponentImpl}

object CakeDI {

  case class Course(code: String, name: String)

  trait AuthServiceComponent { // For expressing dependencies

    def authService : AuthService // Way to obtain the dependency

    trait AuthService { // Interface exposed to the user
      def isAuthorised(userName: String): Boolean
    }

  }

  trait AuthServiceComponentImpl extends AuthServiceComponent {

    override def authService: AuthService = new AuthServiceImpl

    class AuthServiceImpl extends AuthService {
      override     def isAuthorised(userName: String): Boolean = userName.startsWith("J")
    }

  }

  //Individual service / Dependencies
  trait CourseServiceComponent { // For expressing dependencies

    def courseService: CourseService // Way to obtain the dependency

    trait CourseService { // Interface exposed to the user
      def register(course: Course, userName: String, isAuthorised: Boolean): String
    }
  }

  trait CourseServiceComponentImpl extends CourseServiceComponent {

    override def courseService: CourseService = new CourseServiceImpl

    class CourseServiceImpl extends CourseService {
      override def register(course: Course, userName: String, isAuthorised: Boolean) = {
        if (isAuthorised)
          s"User $userName registered for the course: ${course.code}"
        else
          s"User: $userName is not authorised to register for course: ${course.code}"
      }
    }
  }


  trait CourseManagerComponent {

    def courseManager: CourseManager

    trait CourseManager {
      def registerWhenAuthorised(course: Course, userName: String): String
    }
  }

  //Service that need dependencies
  trait CourseManagerComponentImpl extends CourseManagerComponent {

    this: CourseServiceComponent with AuthServiceComponent => //dependency injection

    override def courseManager: CourseManager = new CourseManagerImpl

    class CourseManagerImpl extends CourseManager {
      override def registerWhenAuthorised(course: Course, userName: String): String = {
        val authorised = authService.isAuthorised(userName)
        courseService.register(course, userName, authorised)
      }
    }
  }
}

object ConstructorCake extends App {
  val course = Course("Computer Science", "CS")

  //wiring all together
  val env = new CourseManagerComponentImpl with CourseServiceComponentImpl with AuthServiceComponentImpl

  val register1 = env.courseManager.registerWhenAuthorised(course, "Jack")
  val register2 = env.courseManager.registerWhenAuthorised(course, "Sam")

  println(register1)
  println(register2)
}

//in testing
/**
  *
  * new CourseManagerComponentImpl with CourseServiceComponent with AuthServiceComponent {
  * override def courseService: CourseService = mock[CourseService]
  * override def authService: AuthService = mock[AuthService]
  * }
  */