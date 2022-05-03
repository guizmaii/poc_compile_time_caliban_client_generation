package poc.caliban.tracing.graphql

import caliban.execution.FieldInfo
import caliban.wrappers.Wrapper.FieldWrapper
import caliban.{CalibanError, ResponseValue}
import zio._
import zio.query._
import zio.telemetry.opentelemetry.Tracing
import zio.telemetry.opentelemetry.TracingSyntax._

/**
 * Adapted from https://gist.github.com/frekw/e1d24fec1187278f6eaae5eb8b62f110
 */
object QueryTracer {
  val zqueryTracer: FieldWrapper[Tracing] =
    new FieldWrapper[Tracing] {
      override def wrap[R <: Tracing](
        query: ZQuery[R, CalibanError.ExecutionError, ResponseValue],
        info: FieldInfo,
      ): ZQuery[R, CalibanError.ExecutionError, ResponseValue] = query @@ traced(info.name)
    }

  private def traced[R <: Tracing](name: String): DataSourceAspect[R] =
    new DataSourceAspect[R] {
      override def apply[R1 <: R, A](dataSource: DataSource[R1, A]): DataSource[R1, A] =
        new DataSource[R1, A] {
          override val identifier                                                              = s"${dataSource.identifier} @@ traced"
          override def runAll(requests: Chunk[Chunk[A]]): ZIO[R, Nothing, CompletedRequestMap] = runAll(requests).span(name)
        }
    }
}
