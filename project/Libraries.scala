import sbt._

object Libraries {

  val calibanVersion = "1.1.1"
  val sttpVersion    = "3.3.14"

  val calibanLibs = Seq(
    "com.github.ghostdogpr" %% "caliban" % calibanVersion,
  )

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core"                          % sttpVersion,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttpVersion
  )

}
