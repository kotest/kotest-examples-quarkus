buildscript {
   repositories {
      mavenCentral()
   }
}

plugins {
   java
   id("java-library")
   kotlin("jvm") version "1.4.10"
   kotlin("plugin.allopen") version "1.4.10"
}

val quarkusVersion = "1.4.1.Final"

repositories {
   mavenCentral()
   mavenLocal()
   maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots")
   }
}

kotlin {

   sourceSets {
      main {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation("io.quarkus:quarkus-kotlin:$quarkusVersion")
            implementation("io.quarkus:quarkus-junit5:$quarkusVersion")
         }
      }
      test {
         dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.3.9")
            implementation("io.kotest:kotest-framework-api-jvm:4.2.5")
            implementation("io.kotest:kotest-framework-engine-jvm:4.2.5")
         }
      }
   }
}

allOpen {
   annotation("javax.enterprise.context.ApplicationScoped")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
   kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}
