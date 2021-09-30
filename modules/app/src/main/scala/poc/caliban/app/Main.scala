package poc.caliban.app

import caliban.Http4sAdapter
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.{HttpRoutes, StaticFile}
import zio.{ExitCode, RIO, URIO, ZEnv, ZManaged}

class Main extends zio.App {
  import org.http4s.implicits._
  import zio.interop.catz._

  type LocalTask[A] = RIO[ZEnv, A]

  val server: ZManaged[ZEnv, Throwable, Unit]                =
    for {
      runtime     <- ZManaged.runtime[Any]
      interpreter <- ZManaged.fromEffect(poc.caliban.posts.GraphQLApi.api.interpreter)
      _           <- BlazeServerBuilder[LocalTask](runtime.platform.executor.asEC)
                       .bindHttp(8080, "0.0.0.0")
                       .withHttpApp(
                         Router[LocalTask](
                           "/api/graphql" -> Http4sAdapter.makeHttpService(interpreter),
                           "/ws/graphql"  -> Http4sAdapter.makeWebSocketService(interpreter),
                           "/graphiql"    -> HttpRoutes.liftF(StaticFile.fromResource("/graphiql.html", None))
                         ).orNotFound
                       )
                       .resource
                       .toManagedZIO
    } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = server.useForever.exitCode

}
