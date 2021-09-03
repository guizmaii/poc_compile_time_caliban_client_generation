import sbt._

object Libraries {

  val zioVersion     = "1.0.11"
  val calibanVersion = "1.1.1"
  val sttpVersion    = "3.3.13"

  val zio     = "dev.zio" %% "zio"         % zioVersion
  val prelude = "dev.zio" %% "zio-prelude" % "1.0.0-RC6"

  val calibanLibs = Seq(
    "com.github.ghostdogpr" %% "caliban"        % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-http4s" % calibanVersion
  )

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core"                   % sttpVersion % Test,
    "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % sttpVersion % Test
  )

}
