package poc.caliban.tracing.graphql

import caliban.InputValue.ObjectValue
import caliban.Value.FloatValue.FloatNumber
import caliban.Value.IntValue.IntNumber
import caliban.Value.StringValue
import caliban.execution.{ExecutionRequest, Field}
import caliban.tools.stitching.RemoteQuery
import caliban.wrappers.Wrapper.ExecutionWrapper
import caliban.{CalibanError, GraphQLResponse, InputValue, Value}
import io.opentelemetry.api.trace.{SpanKind, StatusCode}
import zio._
import zio.telemetry.opentelemetry.Tracing

/**
 * Adapted from https://gist.github.com/frekw/e1d24fec1187278f6eaae5eb8b62f110
 */
object SchemaTracer {
  private val errorMapper: PartialFunction[Throwable, StatusCode] = { case _ => StatusCode.ERROR }

  val rootQueryTracer: ExecutionWrapper[Tracing] =
    new ExecutionWrapper[Tracing] {
      override def wrap[R1 <: Tracing](
        f: ExecutionRequest => ZIO[R1, Nothing, GraphQLResponse[CalibanError]]
      ): ExecutionRequest => ZIO[R1, Nothing, GraphQLResponse[CalibanError]] =
        request => {
          val parentField = request.field.fields.head.name

          if (parentField == "__schema") f(request)
          else {
            Tracing.span[R1, Nothing, GraphQLResponse[CalibanError]](
              s"${request.operationType} $parentField",
              SpanKind.INTERNAL,
              errorMapper,
            ) {
              ZIO.foreach_(attributes(request.field)) { case (k, v) => Tracing.setAttribute(k, v) } *>
                f(request)
            }
          }
        }
    }

  private def attributes[T, R](field: Field) = List("query" -> graphQLQuery(field))

  private def graphQLQuery(field: Field): String =
    RemoteQuery.apply(maskField(field)).toGraphQLRequest.query.getOrElse("")

  private def maskArguments(args: Map[String, InputValue]): Map[String, InputValue] =
    args.view.mapValues {
      case _: ObjectValue      => ObjectValue(Map.empty)
      case _: StringValue      => StringValue("")
      case _: Value.IntValue   => IntNumber(0)
      case _: Value.FloatValue => FloatNumber(0f)
      case x                   => x
    }.toMap

  private def maskField(f: Field): Field =
    f.copy(
      arguments = maskArguments(f.arguments),
      fields = f.fields.map(maskField),
    )
}
