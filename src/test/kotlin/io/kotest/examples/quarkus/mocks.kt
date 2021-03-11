package io.kotest.examples.quarkus

import io.quarkus.test.Mock
import javax.enterprise.context.ApplicationScoped

@Mock
@ApplicationScoped
class MockedService : MockableService() {
    override fun greet() = "Welcome"
}
