package generated

import caliban.client.FieldBuilder._
import caliban.client._
import caliban.client.__Value._

object Client {

  type ID = String

  type AuthorName
  object AuthorName {
    def name: SelectionBuilder[AuthorName, String] = _root_.caliban.client.SelectionBuilder.Field("name", Scalar())
  }

  type PostId
  object PostId {
    def id: SelectionBuilder[PostId, String] = _root_.caliban.client.SelectionBuilder.Field("id", Scalar())
  }

  type PostContent
  object PostContent {
    def content: SelectionBuilder[PostContent, String] = _root_.caliban.client.SelectionBuilder.Field("content", Scalar())
  }

  type PostTitle
  object PostTitle {
    def title: SelectionBuilder[PostTitle, String] = _root_.caliban.client.SelectionBuilder.Field("title", Scalar())
  }

  type Post
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

  case class PostContentInput(content: String)
  object PostContentInput {
    implicit val encoder: ArgEncoder[PostContentInput] = new ArgEncoder[PostContentInput] {
      override def encode(value: PostContentInput): __Value =
        __ObjectValue(List("content" -> implicitly[ArgEncoder[String]].encode(value.content)))
    }
  }
  case class AuthorNameInput(name: String)
  object AuthorNameInput  {
    implicit val encoder: ArgEncoder[AuthorNameInput] = new ArgEncoder[AuthorNameInput] {
      override def encode(value: AuthorNameInput): __Value =
        __ObjectValue(List("name" -> implicitly[ArgEncoder[String]].encode(value.name)))
    }
  }
  case class PostTitleInput(title: String)
  object PostTitleInput   {
    implicit val encoder: ArgEncoder[PostTitleInput] = new ArgEncoder[PostTitleInput] {
      override def encode(value: PostTitleInput): __Value =
        __ObjectValue(List("title" -> implicitly[ArgEncoder[String]].encode(value.title)))
    }
  }
  type Query = _root_.caliban.client.Operations.RootQuery
  object Query            {
    def postById[A](id: String)(innerSelection: SelectionBuilder[Post, A])(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootQuery, Option[A]] = _root_.caliban.client.SelectionBuilder
      .Field("postById", OptionOf(Obj(innerSelection)), arguments = List(Argument("id", id, "ID!")(encoder0)))
  }

  type Mutation = _root_.caliban.client.Operations.RootMutation
  object Mutation {
    def createPost[A](authorName: AuthorNameInput, title: PostTitleInput, content: PostContentInput)(
      innerSelection: SelectionBuilder[Post, A]
    )(implicit
      encoder0: ArgEncoder[AuthorNameInput],
      encoder1: ArgEncoder[PostTitleInput],
      encoder2: ArgEncoder[PostContentInput]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Option[A]] = _root_.caliban.client.SelectionBuilder.Field(
      "createPost",
      OptionOf(Obj(innerSelection)),
      arguments = List(
        Argument("authorName", authorName, "AuthorNameInput!")(encoder0),
        Argument("title", title, "PostTitleInput!")(encoder1),
        Argument("content", content, "PostContentInput!")(encoder2)
      )
    )
    def deletePost(
      id: String
    )(implicit encoder0: ArgEncoder[String]): SelectionBuilder[_root_.caliban.client.Operations.RootMutation, Option[Unit]] =
      _root_.caliban.client.SelectionBuilder.Field("deletePost", OptionOf(Scalar()), arguments = List(Argument("id", id, "ID!")(encoder0)))
  }

  type Subscription = _root_.caliban.client.Operations.RootSubscription
  object Subscription {
    def allPostsByAuthor[A](name: String)(innerSelection: SelectionBuilder[Post, A])(implicit
      encoder0: ArgEncoder[String]
    ): SelectionBuilder[_root_.caliban.client.Operations.RootSubscription, Option[A]] = _root_.caliban.client.SelectionBuilder
      .Field("allPostsByAuthor", OptionOf(Obj(innerSelection)), arguments = List(Argument("name", name, "String!")(encoder0)))
  }

}
