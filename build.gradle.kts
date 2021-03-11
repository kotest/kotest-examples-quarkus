plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.20"
    id("io.quarkus")
}

group = "io.kotest"
version = "1.0.0"

val quarkusVersion = "1.4.1.Final"
val kotestVersion = "4.4.3"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-arc")



    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.3.9")
    testImplementation("io.kotest:kotest-framework-api-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-engine-jvm:$kotestVersion")
}


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.jvmTarget = JavaVersion.VERSION_1_8.toString()
        this.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        this.javaParameters = true
    }
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
