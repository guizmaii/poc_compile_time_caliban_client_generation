import com.guizmaii.sbt.DatadogAPM.autoImport.{datadogApmVersion, datadogServiceName}
import com.typesafe.sbt.packager.Keys.{
  bashScriptExtraDefines,
  packageName,
  dockerBaseImage => _,
  dockerExposedPorts => _,
  dockerRepository => _,
  dockerUpdateLatest => _,
}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker, dockerBaseImage, dockerExposedPorts, dockerUpdateLatest}
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal
import sbt.Keys._
import sbt.{Compile, _}

object BuildHelper {

  lazy val commonSettings = Seq(
    libraryDependencies += compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    javacOptions ++= Seq("-source", "11", "-target", "11"),
    scalacOptions --= {
      if (insideCI.value) Nil else Seq("-Xfatal-warnings") // enforced by the pre-push hook too
    },
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Libraries.tests.map(_ % Test),
    (Test / parallelExecution) := true,
    (Test / fork)              := true,
  ) ++ noDoc

  lazy val noDoc = Seq(
    (Compile / doc / sources)                := Seq.empty,
    (Compile / packageDoc / publishArtifact) := false,
  )

  /**
   * Copied from Cats
   */
  lazy val noPublishSettings = Seq(
    publish         := {},
    publishLocal    := {},
    publishM2       := {},
    publishArtifact := false,
  )

  /**
   * Related docs:
   *   - https://github.com/guizmaii/sbt-datadog
   *   - https://docs.datadoghq.com/tracing/setup_overview/setup/java/?tab=containers#configuration
   *   - https://docs.datadoghq.com/tracing/profiler/enabling/java/?tab=commandarguments
   *   - https://docs.datadoghq.com/tracing/profiler/profiler_troubleshooting/#enabling-the-allocation-profiler
   */
  lazy val sbtDatadogSettings =
    Seq(
      datadogApmVersion  := "0.99.0",
      datadogServiceName := "poc_compile_time_caliban_client_generation",
      bashScriptExtraDefines ++= Seq(
        "-Ddd.profiling.enabled=true",
        "-XX:FlightRecorderOptions=stackdepth=256",
        "-Ddd.logs.injection=true",
        "-Ddd.trace.sample.rate=1",
        "-Ddd.profiling.allocation.enabled=true",                 // https://docs.datadoghq.com/tracing/profiler/profiler_troubleshooting/#enabling-the-allocation-profiler
        "-Ddd.profiling.heap.enabled=true",                       // https://docs.datadoghq.com/tracing/profiler/profiler_troubleshooting/#enabling-the-heap-profiler
        "-Ddd.profiling.jfr-template-override-file=comprehensive",// https://docs.datadoghq.com/tracing/profiler/profiler_troubleshooting/#increase-profiler-information-granularity
      ).map(config => s"""addJava "$config""""),
    )

  lazy val dockerSettings = Seq(
    Docker / packageName := "poc_compile_time_caliban_client_generation",
    dockerUpdateLatest   := false,
    dockerExposedPorts   := Seq(8080),
    dockerBaseImage      := "azul/zulu-openjdk:17",
    Universal / javaOptions ++= Seq(
      "-J-XX:ActiveProcessorCount=4", // Overrides the automatic detection mechanism of the JVM that doesn't work very well in k8s.
      "-J-XX:MaxRAMPercentage=80.0",  // 80% * 1280Mi = 1024Mi (See https://github.com/conduktor/conduktor-devtools-builds/pull/96/files#diff-1c0a26888454bc51fc9423622b5d4ee82456b0420f169518a371f3f0e23d443cR67-R70)
      "-J-XX:+ExitOnOutOfMemoryError",
      "-J-XX:+HeapDumpOnOutOfMemoryError",
      "-J-XshowSettings:system",      // https://developers.redhat.com/articles/2022/04/19/java-17-whats-new-openjdks-container-awareness#recent_changes_in_openjdk_s_container_awareness_code
      "-Dfile.encoding=UTF-8",
    ),
  )

}
