import sbt._

object Libraries {

  val calibanVersion       = "2.0.1"
  val sttpVersion          = "3.7.1"
  val zioVersion           = "2.0.0"
  val openTelemetryVersion = "1.16.0"
  val slf4jVersion         = "1.7.36"

  val zio          = "dev.zio" %% "zio"               % zioVersion
  val zioManaged   = "dev.zio" %% "zio-managed"       % zioVersion
  val zioTelemetry = "dev.zio" %% "zio-opentelemetry" % "2.0.0"

  val logging = Seq(
    "dev.zio"             %% "zio-logging-slf4j"        % "2.0.1",
    "ch.qos.logback"       % "logback-classic"          % "1.2.11",
    "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
    "org.slf4j"            % "jul-to-slf4j"             % slf4jVersion,
    "org.slf4j"            % "log4j-over-slf4j"         % slf4jVersion,
    "org.slf4j"            % "jcl-over-slf4j"           % slf4jVersion,
  )

  val tests = Seq(
    "dev.zio" %% "zio-test"          % zioVersion,
    "dev.zio" %% "zio-test-sbt"      % zioVersion,
    "dev.zio" %% "zio-test-magnolia" % zioVersion,
    "dev.zio" %% "zio-interop-cats"  % "22.0.0.0",
  )

  val calibanLib = Seq(
    "com.github.ghostdogpr" %% "caliban"          % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-zio-http" % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-tools"    % calibanVersion,
  )

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
    "com.softwaremill.sttp.client3" %% "zio"  % sttpVersion,
  )

  /**
   * See:
   * - "Setting up OTLP exporters" in https://github.com/open-telemetry/opentelemetry-java-docs
   * - https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/otlp
   */
  val openTelemetryExporter = Seq(
    "io.opentelemetry" % "opentelemetry-api"                % openTelemetryVersion,
    "io.opentelemetry" % "opentelemetry-sdk"                % openTelemetryVersion,
    "io.opentelemetry" % "opentelemetry-exporter-otlp"      % openTelemetryVersion,
    "io.opentelemetry" % "opentelemetry-extension-noop-api" % s"$openTelemetryVersion-alpha",
  )
}
