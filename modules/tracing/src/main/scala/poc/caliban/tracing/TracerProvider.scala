package poc.caliban.tracing

import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.noopapi.NoopOpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.`export`.BatchSpanProcessor
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes
import zio.clock.Clock
import zio.logging.{Logging, log}
import zio.{Has, Managed, RLayer, Task, TaskLayer, ZManaged}

import java.util.concurrent.TimeUnit

object TracerProvider {
  private val instrumentationName = "poc.caliban.datadog.reproducer"

  /**
   * See:
   * - https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/otlp
   */
  private def oltp(
    endpoint: String
  )(serviceName: String, appVersion: String, environment: String): ZManaged[Clock, Throwable, Tracer] =
    for {
      resource       <- Task(
                          Resource
                            .builder()
                            .put(ResourceAttributes.SERVICE_NAME, serviceName)
                            .put(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, environment)
                            .build()
                        ).toManaged_
      spanExporter   <-
        ZManaged.fromAutoCloseable(Task(OtlpGrpcSpanExporter.builder.setTimeout(2, TimeUnit.SECONDS).setEndpoint(endpoint).build()))
      spanProcessor  <-
        ZManaged.fromAutoCloseable(Task(BatchSpanProcessor.builder(spanExporter).build()))
      tracerProvider <-
        Managed.make(
          Task(
            SdkTracerProvider
              .builder()
              .addSpanProcessor(spanProcessor)
              .setResource(resource)
              .build()
          )
        )(provider => Task(provider.shutdown()).orDie)
      openTelemetry  <- Task(OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal()).toManaged_
      tracer         <- Task(openTelemetry.getTracer(instrumentationName, appVersion)).toManaged_
    } yield tracer

  def live(maybeHostIp: Option[String])(
    serviceName: String,
    appVersion: String,
    environment: String,
  ): RLayer[Clock with Logging, Has[Tracer]] =
    maybeHostIp match {
      case Some(hostIp) =>
        (log.info(s"OpenTelemetry activated. `HOST_IP` is `$hostIp`").toManaged_ *>
          oltp(s"http://$hostIp:4317")(serviceName, appVersion, environment)).toLayer
      case None         =>
        (log.info("OpenTelemetry not activated. No `HOST_IP` envvar detected") *> noOpTask).toLayer
    }

  def noOp: TaskLayer[Has[Tracer]] = noOpTask.toLayer

  private val noOpTask = Task(NoopOpenTelemetry.getInstance().getTracer(instrumentationName))

}
