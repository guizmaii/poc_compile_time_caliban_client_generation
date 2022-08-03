package poc.caliban.client.generated.posts.splitted

import caliban.client.FieldBuilder._
import caliban.client._

object Query {
  def postById[A](id: String)(innerSelection: SelectionBuilder[Post, A])(
    implicit encoder0: ArgEncoder[String]
  ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, scala.Option[A]] = _root_.caliban.client.SelectionBuilder
    .Field("postById", OptionOf(Obj(innerSelection)), arguments = List(Argument("id", id, "ID!")(encoder0)))
}
