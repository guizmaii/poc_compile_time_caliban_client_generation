package poc.caliban.client.generated.posts.splitted

import caliban.client.FieldBuilder._
import caliban.client._

object Subscription {
  def allPostsByAuthor[A](name: String)(innerSelection: SelectionBuilder[Post, A])(
    implicit encoder0: ArgEncoder[String]
  ): SelectionBuilder[_root_.caliban.client.Operations.RootSubscription, scala.Option[A]] = _root_.caliban.client.SelectionBuilder
    .Field("allPostsByAuthor", OptionOf(Obj(innerSelection)), arguments = List(Argument("name", name, "String!")(encoder0)))
}
