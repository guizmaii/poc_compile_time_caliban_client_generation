package poc.caliban.client.generated.posts.splitted

import caliban.client.FieldBuilder._
import caliban.client._

object Mutation {
  def createPost[A](authorName: AuthorNameInput, title: PostTitleInput, content: PostContentInput)(
    innerSelection: SelectionBuilder[Post, A]
  )(
    implicit encoder0: ArgEncoder[AuthorNameInput],
    encoder1: ArgEncoder[PostTitleInput],
    encoder2: ArgEncoder[PostContentInput],
  ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[A]] = _root_.caliban.client.SelectionBuilder.Field(
    "createPost",
    OptionOf(Obj(innerSelection)),
    arguments = List(
      Argument("authorName", authorName, "AuthorNameInput!")(encoder0),
      Argument("title", title, "PostTitleInput!")(encoder1),
      Argument("content", content, "PostContentInput!")(encoder2),
    ),
  )
  def deletePost(
    id: String
  )(implicit encoder0: ArgEncoder[String]): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, scala.Option[Unit]] =
    _root_.caliban.client.SelectionBuilder.Field("deletePost", OptionOf(Scalar()), arguments = List(Argument("id", id, "ID!")(encoder0)))
}

