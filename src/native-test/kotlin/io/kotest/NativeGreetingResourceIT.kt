package io.kotest

import io.quarkus.test.junit.NativeImageTest

@NativeImageTest
class NativeGreetingResourceIT : GreetingResourceTest()