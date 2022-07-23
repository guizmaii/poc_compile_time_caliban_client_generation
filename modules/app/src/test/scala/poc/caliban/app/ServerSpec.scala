package poc.caliban.app

import caliban.client.Operations.{RootMutation, RootQuery}
import caliban.client.SelectionBuilder
import poc.caliban.client.generated.posts.CalibanClient._
import poc.caliban.posts.PostService
import poc.caliban.tracing.TracerProvider
import sttp.capabilities
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.SttpBackend
import sttp.model.Uri
import zio._
import zio.telemetry.opentelemetry.Tracing
import zio.test.Assertion._
import zio.test.TestAspect.sequential
import zio.test.{ZIOSpecDefault, _}

import java.util.UUID

//noinspection SimplifyAssertInspection
object ServerSpec extends ZIOSpecDefault {
  import sttp.client3.httpclient.zio.HttpClientZioBackend

  val tracing: TaskLayer[Tracing] =
    ZLayer.make[Tracing](
      Tracing.live,
      TracerProvider.noOp,
    )

  private val apiUrl: Uri = Uri.parse("http://localhost:8080/api/graphql").toOption.get

  def withServer(
    test: SttpBackend[Task, ZioStreams with WebSockets] => ZIO[TestEnvironment, Throwable, TestResult]
  ): ZIO[TestEnvironment with Scope, Throwable, TestResult] =
    (
      for {
        backend: SttpBackend[Task, ZioStreams with capabilities.WebSockets] <- HttpClientZioBackend.scoped()
        _                                                                   <- Main.server
        response                                                            <- test(backend)
      } yield response
    ).provideSome[TestEnvironment with zio.Scope](PostService.layer, tracing)

  val createMillPostMutation: SelectionBuilder[RootMutation, Option[String]] =
    Mutation
      .createPost(
        AuthorNameInput("John Stuart Mill"),
        PostTitleInput("Utilitarianism"),
        PostContentInput(
          "It is better to be a human being dissatisfied than a pig satisfied; better to be Socrates dissatisfied than a fool satisfied. And if the fool, or the pig, is of a different opinion, it is only because they only know their own side of the question."
        ),
      )(Post.id(PostId.id))

  private val calibanServerSpec =
    suite("Caliban server Spec")(
      test("truthiness")(assert(true)(isTrue)),
      test("Create a post returns a 200") {
        withServer { backend =>
          val response = createMillPostMutation.toRequest(apiUrl).send(backend)
          assertZIO(response.map(_.code.code))(equalTo(200))
        }
      },
      test("Fetch a non existing post returns None") {
        withServer { backend =>
          val query: SelectionBuilder[RootQuery, Option[String]] = Query.postById(UUID.randomUUID().toString)(Post.id(PostId.id))

          val response = query.toRequest(apiUrl).send(backend)

          assertZIO(response.map(_.body).absolve)(isNone)
        }
      },
      test("Fetch an existing post returns Some(_)") {
        withServer { backend =>
          def query(id: String): SelectionBuilder[RootQuery, Option[String]] = Query.postById(id)(Post.author(AuthorName.name))

          val result: ZIO[TestEnvironment, Throwable, Option[String]] =
            for {
              id: String <- createMillPostMutation.toRequest(apiUrl).send(backend).map(_.body).absolve.map(_.get)
              author     <- query(id).toRequest(apiUrl).send(backend).map(_.body).absolve
            } yield author

          assertZIO(result)(isSome(equalTo("John Stuart Mill")))
        }
      },
    ) @@ sequential

  override def spec: Spec[TestEnvironment with Scope, Throwable] =
    suite("Server Spec")(
      calibanServerSpec
    )
}
