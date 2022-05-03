package poc.caliban.app

import caliban._
import poc.caliban.posts.PostService
import poc.caliban.tracing.TracerProvider
import zhttp.http.Middleware.cors
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio.clock.Clock
import zio.logging.slf4j.Slf4jLogger
import zio.magic._
import zio.system.env
import zio.telemetry.opentelemetry.Tracing
import zio.{ExitCode, Has, RIO, URIO, ZEnv, ZLayer, ZManaged}

object Main extends zio.App {

  type AppEnv       = ZEnv with Has[PostService] with Tracing
  type LocalTask[A] = RIO[AppEnv, A]

  private def routes(interpreter: GraphQLInterpreter[AppEnv, CalibanError]): HttpApp[AppEnv with Clock, Throwable] =
    Http.collectHttp[Request] {
      case Method.GET -> !! / "health" / "check" => Http.ok
      case Method.GET -> !! / "health" / "ping"  => Http.ok
      case _ -> !! / "api" / "graphql"           => ZHttpAdapter.makeHttpService(interpreter)
      case _ -> !! / "ws" / "graphql"            => ZHttpAdapter.makeWebSocketService(interpreter)
    } @@ cors()

  private def startZioHttpServer[R <: Has[_]](
    port: Int,
    http: HttpApp[R, Throwable],
  ): ZManaged[R, Throwable, Unit] =
    Server(http)
      .withPort(port)
      .make
      .provideSomeLayer[R](EventLoopGroup.auto(0) ++ ServerChannelFactory.auto)
      .unit

  val server: ZManaged[ZEnv with Has[PostService] with Tracing, Throwable, Unit] =
    (
      for {

        interpreter <- poc.caliban.posts.GraphQLApi.api.interpreter.toManaged_
        _           <- startZioHttpServer(8080, routes(interpreter))
      } yield ()
    )

  private def layers(maybeHostIp: Option[String]): ZLayer[ZEnv, Nothing, Has[PostService] with Tracing] =
    ZLayer.wireSome[ZEnv, Has[PostService] with Tracing](
      Tracing.live,
      PostService.layer,
      TracerProvider
        .live(maybeHostIp)(serviceName = "poc-caliban", appVersion = "0.0.1", environment = "staging")
        .orDie,
      Slf4jLogger.make((_, m) => m),
    )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (
      for {
        maybeHostIp <- env("HOST_IP").toManaged_.orDie
        _           <- server.provideSomeLayer[ZEnv](layers(maybeHostIp))
      } yield ()
    ).useForever.exitCode

}
