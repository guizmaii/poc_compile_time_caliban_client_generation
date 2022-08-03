package poc.caliban.client.generated.posts.splitted

import caliban.client.FieldBuilder._
import caliban.client._

object Post {
  def id[A](innerSelection: SelectionBuilder[PostId, A]): SelectionBuilder[Post, A]           =
    _root_.caliban.client.SelectionBuilder.Field("id", Obj(innerSelection))
  def author[A](innerSelection: SelectionBuilder[AuthorName, A]): SelectionBuilder[Post, A]   =
    _root_.caliban.client.SelectionBuilder.Field("author", Obj(innerSelection))
  def title[A](innerSelection: SelectionBuilder[PostTitle, A]): SelectionBuilder[Post, A]     =
    _root_.caliban.client.SelectionBuilder.Field("title", Obj(innerSelection))
  def content[A](innerSelection: SelectionBuilder[PostContent, A]): SelectionBuilder[Post, A] =
    _root_.caliban.client.SelectionBuilder.Field("content", Obj(innerSelection))
}
