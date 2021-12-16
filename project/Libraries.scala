import sbt._

object Libraries {

  val calibanVersion = "1.3.1"
  val sttpVersion    = "3.3.18"
  val http4sVersion  = "0.23.7"
  val zioVersion     = "1.0.13"

  val zioMagic = "io.github.kitlangton" %% "zio-magic" % "0.3.11"

  val http4s = Seq(
    "org.http4s" %% "http4s-core"         % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion,
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
  )

  val tests = Seq(
    "dev.zio" %% "zio-test"          % zioVersion,
    "dev.zio" %% "zio-test-sbt"      % zioVersion,
    "dev.zio" %% "zio-test-magnolia" % zioVersion,
    "dev.zio" %% "zio-interop-cats"  % "3.2.9.0",
  )

  val calibanLib = Seq(
    "com.github.ghostdogpr" %% "caliban"        % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-http4s" % calibanVersion,
  )

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core"                   % sttpVersion,
    "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % sttpVersion
  )

}
