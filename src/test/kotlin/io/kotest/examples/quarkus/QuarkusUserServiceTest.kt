package io.kotest.examples.quarkus

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.quarkus.test.junit.QuarkusTest
import javax.inject.Inject

@QuarkusTest
class QuarkusUserServiceTest : FunSpec() {

   @Inject
   lateinit var userService: UserService

   init {
      test("user service should be injected") {
         userService.repository.findUser().name shouldBe "system_user"
      }
   }
}
