import sbt._

object Libraries {

  val calibanVersion       = "1.4.0"
  val sttpVersion          = "3.6.2"
  val http4sVersion        = "0.23.11"
  val zioVersion           = "1.0.15"
  val openTelemetryVersion = "1.14.0"
  val slf4jVersion         = "1.7.36"

  val zio          = Seq(
    "dev.zio"              %% "zio"       % zioVersion,
    "io.github.kitlangton" %% "zio-magic" % "0.3.12",
  )
  val zioMagic     = "io.github.kitlangton" %% "zio-magic"         % "0.3.12"
  val zioTelemetry = "dev.zio"              %% "zio-opentelemetry" % "1.0.0"

  val logging = Seq(
    "dev.zio"             %% "zio-logging-slf4j"        % "0.5.14",
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
    "dev.zio" %% "zio-interop-cats"  % "3.2.9.1",
  )

  val calibanLib = Seq(
    "com.github.ghostdogpr" %% "caliban"          % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-zio-http" % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-tools"    % calibanVersion,
  )

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
    "com.softwaremill.sttp.client3" %% "zio1" % sttpVersion,
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
