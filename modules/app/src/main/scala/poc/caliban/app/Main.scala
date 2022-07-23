package poc.caliban.app

import caliban._
import poc.caliban.posts.PostService
import poc.caliban.tracing.TracerProvider
import zhttp.http.Middleware.cors
import zhttp.http._
import zhttp.service.Server
import zio.System.env
import zio.telemetry.opentelemetry.Tracing
import zio.{RIO, Scope, ULayer, ZIO, ZIOAppArgs, ZLayer}

object Main extends zio.ZIOAppDefault {

  type AppEnv       = PostService with Tracing
  type LocalTask[A] = RIO[AppEnv, A]

  private def routes(interpreter: GraphQLInterpreter[AppEnv, CalibanError]): HttpApp[AppEnv, Throwable] =
    Http.collectHttp[Request] {
      case Method.GET -> !! / "health" / "check" => Http.ok
      case Method.GET -> !! / "health" / "ping"  => Http.ok
      case _ -> !! / "api" / "graphql"           => ZHttpAdapter.makeHttpService(interpreter)
      case _ -> !! / "ws" / "graphql"            => ZHttpAdapter.makeWebSocketService(interpreter)
    } @@ cors()

  private def startZioHttpServer[R](
    port: Int,
    http: HttpApp[R, Throwable],
  ): ZIO[R, Throwable, Server.Start] =
    Server(http)
      .withPort(port)
      .startDefault

  val server: ZIO[PostService with Tracing, Throwable, Unit] =
    for {

      interpreter <- poc.caliban.posts.GraphQLApi.api.interpreter
      _           <- startZioHttpServer(8080, routes(interpreter))
    } yield ()

  private def layers(maybeHostIp: Option[String]): ULayer[PostService with Tracing] =
    ZLayer.make[PostService with Tracing](
      Tracing.live,
      PostService.layer,
      TracerProvider
        .live(maybeHostIp)(serviceName = "poc-caliban", appVersion = "0.0.1", environment = "staging")
        .orDie,
    )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      maybeHostIp <- env("HOST_IP")
      _           <- server.provideSomeLayer[Scope](layers(maybeHostIp))
    } yield ()

}
