package io.kotest.examples.quarkus

import io.kotest.common.runBlocking
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import org.junit.jupiter.api.extension.TestWatcher
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.Optional

/**
 * Adapts JUnit Platform Extensions into Kotest Listeners/Extensions.
 *
 * Supported JUnit Extensions:
 * - [BeforeEachCallback]
 * - [BeforeAllCallback]
 * - [AfterEachCallback]
 * - [AfterAllCallback]
 * - [BeforeTestExecutionCallback]
 * - [AfterTestExecutionCallback]
 * - [TestInstancePostProcessor]
 * - [TestWatcher]
 * - [TestInstancePreDestroyCallback]
 * - [ExecutionCondition]
 */
class JUnitExtensionAdapter(private val extension: Extension) : TestListener, SpecExtension {

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {

      if (extension is InvocationInterceptor) {

         extension.interceptTestClassConstructor(
            InvocationInterceptor.Invocation {
               runBlocking {
                  execute(spec)
               }
               null
            },
            object : ReflectiveInvocationContext<Constructor<Any>> {
               override fun getArguments(): MutableList<Any> = mutableListOf()
               override fun getExecutable(): Constructor<Any>? = null
               override fun getTargetClass(): Class<*> = spec::class.java
               override fun getTarget(): Optional<Any> = Optional.empty()
            },
            KotestExtensionContext(spec, null),
         )

//         extension.interceptBeforeAllMethod(
//            object : InvocationInterceptor.Invocation<Void> {
//               override fun proceed(): Void {
//                  runBlocking {
//                     execute(spec)
//                  }
//                  return Unit as Void
//               }
//
//               override fun skip() {
//               }
//            },
//            object : ReflectiveInvocationContext<Method> {
//               override fun getArguments(): MutableList<Any> = mutableListOf()
//               override fun getExecutable(): Method? = null
//               override fun getTargetClass(): Class<*> = spec::class.java
//               override fun getTarget(): Optional<Any> = Optional.empty()
//            },
//            KotestExtensionContext(spec, null),
//         )
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      val context = KotestExtensionContext(testCase.spec, testCase)
      if (extension is BeforeTestExecutionCallback) {
         extension.beforeTestExecution(context)
      }
      if (extension is BeforeEachCallback) {
         extension.beforeEach(context)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val context = KotestExtensionContext(testCase.spec, testCase)
      if (extension is AfterEachCallback) {
         extension.afterEach(context)
      }
      if (extension is AfterTestExecutionCallback) {
         extension.afterTestExecution(context)
      }
      if (extension is TestWatcher) {
         when (result.status) {
            TestStatus.Ignored -> extension.testDisabled(context, Optional.ofNullable(result.reason))
            TestStatus.Success -> extension.testSuccessful(context)
            TestStatus.Error -> extension.testAborted(context, result.error)
            TestStatus.Failure -> extension.testFailed(context, result.error)
         }
      }
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (extension is TestInstancePostProcessor) {
         extension.postProcessTestInstance(spec, KotestExtensionContext(spec, null))
      }
      if (extension is BeforeAllCallback) {
         extension.beforeAll(KotestExtensionContext(spec, null))
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (extension is AfterAllCallback) {
         extension.afterAll(KotestExtensionContext(spec, null))
      }
      if (extension is TestInstancePreDestroyCallback) {
         extension.preDestroyTestInstance(KotestExtensionContext(spec, null))
      }
   }
}
