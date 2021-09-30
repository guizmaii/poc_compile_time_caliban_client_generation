import BuildHelper._
import Libraries._
import sbt.librarymanagement.Resolver

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization               := "Conduktor"
ThisBuild / homepage                   := Some(url("https://www.conduktor.io/"))
ThisBuild / licenses                   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / version                    := "0.0.1"
ThisBuild / scalaVersion               := "2.13.6"
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
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

// ### App Modules ###

/**
 * `root` is a "meta module". It's the "main module" of this project but doesn't have a physical existence. It represents the "current
 * project" if you prefer, composed of modules.
 *
 * The `aggregate` setting will instruct sbt that when you're launching an sbt command, you want it applied to all the aggregated modules
 */
lazy val root           =
  Project(id = "poc_compile_time_caliban_client_generation", base = file("."))
    .settings(noDoc: _*)
    .settings(noPublishSettings: _*)
    .aggregate(
      app,
      posts,
      potatoes,
      clients,
      calibanClients,
    )

lazy val app            =
  project
    .in(file("modules/app"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= Seq(zioMagic) ++ http4s ++ sttp.map(_ % Test))
    .dependsOn(posts, calibanClients % Test)

lazy val posts          =
  project
    .in(file("modules/posts"))
    .enablePlugins(CompileTimeCalibanServerPlugin)
    .settings(
      Compile / ctCalibanServer / ctCalibanServerSettings ++=
        Seq(
          "poc.caliban.posts.GraphQLApi.api" -> ClientGenerationSettings.default,
          "poc.caliban.posts.GraphQLApi.api" ->
            ClientGenerationSettings(
              packageName = "poc.caliban.client.generated.posts",
              clientName = "CalibanClient",
            ),
          "poc.caliban.posts.GraphQLApi.api" ->
            ClientGenerationSettings(
              packageName = "poc.caliban.client.generated.posts.splitted",
              splitFiles = true
            )
        )
    )
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= calibanLib)

lazy val potatoes       =
  project
    .in(file("modules/potatoes"))
    .enablePlugins(CompileTimeCalibanServerPlugin)
    .settings(
      Compile / ctCalibanServer / ctCalibanServerSettings :=
        Seq(
          "poc.caliban.potatoes.PotatoesApi.api" ->
            ClientGenerationSettings(
              packageName = "poc.caliban.client.generated.potatoes",
              splitFiles = true
            )
        )
    )
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= calibanLib)

lazy val clients        =
  project
    .in(file("modules/clients"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= sttp)
    .dependsOn(calibanClients)

lazy val calibanClients =
  project
    .withId("caliban-clients")
    .in(file("modules/caliban-clients"))
    .settings(commonSettings: _*)
    .enablePlugins(CompileTimeCalibanClientPlugin)
    .settings(
      Compile / ctCalibanClient / ctCalibanClientsSettings := Seq(posts, potatoes)
    )
