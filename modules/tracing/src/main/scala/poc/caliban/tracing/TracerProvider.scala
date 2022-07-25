package poc.caliban.tracing

import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.noopapi.NoopOpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.`export`.BatchSpanProcessor
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes
import zio._
import zio.managed.{ZManaged, ZManagedZIOSyntax}

import java.util.concurrent.TimeUnit

object TracerProvider {
  private val instrumentationName = "poc.caliban.datadog.reproducer"

  /**
   * See:
   * - https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/otlp
   */
  private def oltp(
    endpoint: String
  )(serviceName: String, appVersion: String, environment: String): ZManaged[Any, Throwable, Tracer] =
    for {
      resource       <- ZIO
                          .attempt(
                            Resource
                              .builder()
                              .put(ResourceAttributes.SERVICE_NAME, serviceName)
                              .put(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, environment)
                              .build()
                          )
                          .toManaged
      spanExporter   <-
        ZManaged.fromAutoCloseable(ZIO.attempt(OtlpGrpcSpanExporter.builder.setTimeout(2, TimeUnit.SECONDS).setEndpoint(endpoint).build()))
      spanProcessor  <-
        ZManaged.fromAutoCloseable(ZIO.attempt(BatchSpanProcessor.builder(spanExporter).build()))
      tracerProvider <-
        ZManaged.acquireReleaseWith(
          ZIO.attempt(
            SdkTracerProvider
              .builder()
              .addSpanProcessor(spanProcessor)
              .setResource(resource)
              .build()
          )
        )(provider => ZIO.attempt(provider.shutdown()).orDie)
      openTelemetry  <- ZIO.attempt(OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal()).toManaged
      tracer         <- ZIO.attempt(openTelemetry.getTracer(instrumentationName, appVersion)).toManaged
    } yield tracer

  def live(maybeHostIp: Option[String])(
    serviceName: String,
    appVersion: String,
    environment: String,
  ): TaskLayer[Tracer] =
    maybeHostIp match {
      case Some(hostIp) =>
        (ZIO.logInfo(s"OpenTelemetry activated. `HOST_IP` is `$hostIp`").toManaged *>
          oltp(s"http://$hostIp:4317")(serviceName, appVersion, environment)).toLayer
      case None         =>
        ZLayer.fromZIO(ZIO.logInfo("OpenTelemetry not activated. No `HOST_IP` envvar detected") *> noOpTask)
    }

  def noOp: TaskLayer[Tracer] = ZLayer.fromZIO(noOpTask)

  private val noOpTask = ZIO.attempt(NoopOpenTelemetry.getInstance().getTracer(instrumentationName))

}
