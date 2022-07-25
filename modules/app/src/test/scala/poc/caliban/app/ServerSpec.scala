package poc.caliban.app

import caliban.client.Operations.{RootMutation, RootQuery}
import caliban.client.SelectionBuilder
import poc.caliban.client.generated.posts.CalibanClient._
import poc.caliban.posts.PostService
import poc.caliban.tracing.TracerProvider
import sttp.model.Uri
import zhttp.http.RHttpApp
import zio._
import zio.telemetry.opentelemetry.Tracing
import zio.test.Assertion._
import zio.test.TestAspect.sequential
import zio.test.{ZIOSpecDefault, _}

import java.nio.charset.StandardCharsets
import java.util.UUID

//noinspection SimplifyAssertInspection
object ServerSpec extends ZIOSpecDefault {

  final case object Boom extends RuntimeException("BOOM")
  def boom: Throwable = Boom

  import poc.caliban.app.utils.ZioHttpClient._

  val tracing: TaskLayer[Tracing] =
    ZLayer.make[Tracing](
      Tracing.live,
      TracerProvider.noOp,
    )

  private val apiUrl: Uri = Uri.parse("http://localhost:8080/api/graphql").toOption.get

  def withServer(
    test: RHttpApp[Any] => ZIO[TestEnvironment, Throwable, TestResult]
  ): ZIO[TestEnvironment with Scope, Throwable, TestResult] =
    for {
      routes <- Main.makeRoutes
      app     = routes.provideLayer(PostService.layer ++ tracing)
      result <- test(app(_).flattenErrorOption(boom))
    } yield result

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
        withServer { app =>
          val response = app(createMillPostMutation.toZioRequest(apiUrl))

          assertZIO(response.map(_.status.code))(equalTo(200))
        }
      },
      test("Fetch a non existing post returns None") {
        withServer { app =>
          val query: SelectionBuilder[RootQuery, Option[String]] = Query.postById(UUID.randomUUID().toString)(Post.id(PostId.id))
          val response                                           = app(query.toZioRequest(apiUrl))

          assertZIO(response.map(_.status.code))(equalTo(404))
        }
      },
      test("Fetch an existing post returns Some(_)") {
        withServer { app =>
          def query(id: String): SelectionBuilder[RootQuery, Option[String]] = Query.postById(id)(Post.author(AuthorName.name))

          val result: ZIO[TestEnvironment, Throwable, Option[String]] =
            for {
              id: String <- app(createMillPostMutation.toZioRequest(apiUrl))
                              .flatMap(_.body)
                              .map(bytes => new String(bytes.toArray, StandardCharsets.UTF_8))
              _          <- ZIO.debug("--- TODO: $id")
              author     <- app(query(id).toZioRequest(apiUrl))
                              .flatMap(_.body)
                              .map(bytes => new String(bytes.toArray, StandardCharsets.UTF_8))
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
