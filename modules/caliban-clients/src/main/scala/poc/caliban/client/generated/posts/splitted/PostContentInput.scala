package poc.caliban.client.generated.posts.splitted

import caliban.client._
import caliban.client.__Value._

final case class PostContentInput(content: String)
object PostContentInput {
  implicit val encoder: ArgEncoder[PostContentInput] = new ArgEncoder[PostContentInput] {
    override def encode(value: PostContentInput): __Value =
      __ObjectValue(List("content" -> implicitly[ArgEncoder[String]].encode(value.content)))
  }
}

