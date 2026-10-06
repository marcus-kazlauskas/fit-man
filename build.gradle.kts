import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

val springdocStarterUi: String by project
val fitVersion: String by project
val geographicLibVersion: String by project
val mockitoKotlinVersion: String by project

val apiSpecDir: String by project
val apiGenDir: String by project

plugins {
    val kotlinVersion = "2.3.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.openapi.generator") version "7.22.0"
    jacoco
}

group = "fit.man"
version = "2.0.0"

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocStarterUi")

    implementation("com.garmin:fit:$fitVersion")

    implementation("net.sf.geographiclib:GeographicLib-Java:$geographicLibVersion")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    testRuntimeOnly("com.h2database:h2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

ktlint {
    filter {
        val generatedSourcesPath = "$apiGenDir/"
        exclude { entry -> entry.file.path.contains(generatedSourcesPath) }
    }
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir$apiSpecDir/openapi-fit-man.yaml")
    outputDir.set("$rootDir$apiGenDir")
    apiPackage.set("fit.man.app.api")
    modelPackage.set("fit.man.app.api.model")
    templateDir.set("$rootDir$apiSpecDir/templates")
    validateSpec.set(true)
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useSpringBoot4" to "true",
            "useJakartaEe" to "true",
            "exceptionHandler" to "false",
        ),
    )
}

sourceSets {
    main {
        kotlin.srcDir("$rootDir$apiGenDir/src/main/kotlin")
    }
}

val jacocoExcludes =
    listOf(
        "**/api/*Api.class",
        "**/api/*Util.class",
        "**/api/model/**",
        "org/openapitools/**",
    )

val openApiGenerateTask = tasks.named<GenerateTask>("openApiGenerate")

val copyOpenApiToResources by tasks.registering(Copy::class) {
    description = "copy openapi files to resources"
    from("$rootDir$apiSpecDir")
    into("$rootDir/build/resources/main/static")
    include("*.yaml")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) { exclude(jacocoExcludes) }
            },
        ),
    )
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) { exclude(jacocoExcludes) }
            },
        ),
    )
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.test {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.compileKotlin {
    dependsOn(openApiGenerateTask)
}

tasks.named("runKtlintCheckOverMainSourceSet") {
    dependsOn(openApiGenerateTask)
}

tasks.named("runKtlintFormatOverMainSourceSet") {
    dependsOn(openApiGenerateTask)
}

tasks.processResources {
    dependsOn(copyOpenApiToResources)
}

tasks.bootRun {
    dependsOn("ktlintMainSourceSetCheck")
}
