package poc.caliban.app.utils

import caliban.client.Operations.IsOperation
import caliban.client.{GraphQLRequest, SelectionBuilder}
import sttp.model.Uri
import zhttp.http._

object ZioHttpClient {

  implicit final class SelectionBuilderOps[-Origin, +A](private val selectionBuilder: SelectionBuilder[Origin, A]) extends AnyVal {
    def toZioRequest[Origin1 <: Origin](
      uri: Uri,
      useVariables: Boolean = false,
      queryName: Option[String] = None,
      dropNullInputValues: Boolean = false,
    )(implicit ev: IsOperation[Origin1]): Request =
      Request(
        version = Version.`HTTP/1.1`,
        method = Method.POST,
        url = URL.fromString(uri.toString).getOrElse(throw _),
        headers = Headers.empty,
        data = HttpData.fromString {
          GraphQLRequest
            .encoder(selectionBuilder.toGraphQL(useVariables, queryName, dropNullInputValues))
            .noSpaces
        },
      )
  }

}
