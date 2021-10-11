package poc.caliban.client.generated.posts.splitted

import caliban.client._
import caliban.client.__Value._

case class AuthorNameInput(name: String)
object AuthorNameInput {
  implicit val encoder: ArgEncoder[AuthorNameInput] = new ArgEncoder[AuthorNameInput] {
    override def encode(value: AuthorNameInput): __Value =
      __ObjectValue(List("name" -> implicitly[ArgEncoder[String]].encode(value.name)))
  }
}
