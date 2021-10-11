package poc.caliban.client.generated.posts.splitted

import caliban.client._
import caliban.client.__Value._

case class PostTitleInput(title: String)
object PostTitleInput {
  implicit val encoder: ArgEncoder[PostTitleInput] = new ArgEncoder[PostTitleInput] {
    override def encode(value: PostTitleInput): __Value =
      __ObjectValue(List("title" -> implicitly[ArgEncoder[String]].encode(value.title)))
  }
}
