import BuildHelper._
import Libraries._
import sbt.librarymanagement.Resolver

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization               := "io.guizmaii.poc"
ThisBuild / homepage                   := Some(url("https://www.conduktor.io/"))
ThisBuild / licenses                   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / version                    := "0.0.1"
ThisBuild / scalaVersion               := "2.12.13"                   // Must stay 2.12 because the plugin is compiled with 2.12
ThisBuild / scalafmtCheck              := true
ThisBuild / scalafmtSbtCheck           := true
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbOptions += "-P:semanticdb:synthetics:on"
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision // use Scalafix compatible version
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / scalafixDependencies ++= List(
  "com.github.vovapolu" %% "scaluzzi" % "0.1.20"
)
ThisBuild / resolvers += Resolver.mavenLocal

// ### App Modules ###

/**
 * `root` is a "meta module". It's the "main module" of this project but doesn't have a physical existence. It represents the "current
 * project" if you prefer, composed of modules.
 *
 * The `aggregate` setting will instruct sbt that when you're launching an sbt command, you want it applied to all the aggregated modules
 */
lazy val root =
  Project(id = "poc_compile_time_caliban_client_generation", base = file("."))
    .settings(noDoc: _*)
    .settings(noPublishSettings: _*)
    .aggregate(
      server,
      client,
      calibanClient,
    )

lazy val server =
  project
    .in(file("modules/server"))
    .enablePlugins(CompileTimeCalibanServerPlugin)
    .settings(Compile / ctCalibanServer / ctCalibanServerApiRefs := Seq("io.guizmaii.poc.caliban.server.GraphQLApi.api"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= calibanLibs)

lazy val client =
  project
    .in(file("modules/client"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= sttp)
    .dependsOn(calibanClient)

lazy val calibanClient =
  project
    .withId("caliban-client")
    .in(file("modules/caliban-client"))
    .settings(commonSettings: _*)
    .enablePlugins(CompileTimeCalibanClientPlugin)
    .settings(
      Compile / ctCalibanClient / ctCalibanClientsSettings +=
        server -> Seq(
          GenerateClientSettings(
            clientName = "CalibanClient",
            packageName = "io.guizmaii.poc.caliban.client.generated",
          )
        )
    )
