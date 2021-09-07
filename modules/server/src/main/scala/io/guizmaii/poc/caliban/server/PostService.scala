package io.guizmaii.poc.caliban.server

import zio._
import zio.prelude.Equal
import zio.stream.ZStream

import java.util.UUID

final case class PostId(id: UUID)
final case class AuthorName(name: String)
object AuthorName {
  implicit val eq: Equal[AuthorName] = Equal.default
}
final case class PostTitle(title: String)
final case class PostContent(content: String)

final case class Post(id: PostId, author: AuthorName, title: PostTitle, content: PostContent)

sealed trait PostServiceError extends Throwable
object PostServiceError {
  case object DbIsKo extends PostServiceError
}

trait PostService {
  def findById(id: PostId): IO[PostServiceError, Post]
  def createPost(author: AuthorName, title: PostTitle, content: PostContent): IO[PostServiceError, Post]
  def deletePost(id: PostId): IO[PostServiceError, Unit]
  def all: ZStream[Any, PostServiceError, Post]
}

object PostService extends Accessible[PostService]
