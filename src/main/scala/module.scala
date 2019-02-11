import scala.concurrent.ExecutionContext

object module {

  import scala.concurrent.Future

  object module {

    implicit val ec= ExecutionContext.global

    case class Host(value: String) extends AnyVal
    case class Port(value: Int) extends AnyVal
    case class Config(host: Host, port: Port)

    case class User(id: String, name: String, email: String)

    trait Repository {
      def users: Future[List[User]]
    }

    class PostgreSQLRepository(config: Config) extends Repository {
      //Connect to DB to get users
      override def users: Future[List[User]] = ???
    }

    class InMemoryRepository extends Repository {
      override val users: Future[List[User]] = Future.successful(List(
        User("1", "John", "john@test.com"),
        User("2", "Tom", "tom@test.com")
      ))
    } //for unit testing

    trait UserService {
      def getUser(id: String): Future[Option[User]]
      def create(user: User): Future[Unit]
      def delete(user: User): Future[Unit]
    }

    class DefaultUserService(repo: Repository) extends UserService {

      override def getUser(id: String): Future[Option[User]] = repo.users.map(_.find(_.id == id))

      override def create(user: User): Future[Unit] = ???

      override def delete(user: User): Future[Unit] = ???
    }
  }



}
