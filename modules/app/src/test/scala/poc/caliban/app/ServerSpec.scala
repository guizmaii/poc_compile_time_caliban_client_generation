package poc.caliban.app

import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

object ServerSpec extends DefaultRunnableSpec {

  private val calibanServerSpec                  =
    suite("Caliban server Spec")(
      test("truthiness")(assert(true)(isTrue))
    )

  override def spec: ZSpec[TestEnvironment, Any] =
    suite("Server Spec")(
      calibanServerSpec,
    )
}
