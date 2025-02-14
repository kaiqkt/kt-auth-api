plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.openapi.generator") version "7.9.0"
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

springBoot {
	buildInfo()
}

jacoco {
	toolVersion = "0.8.12"
	reportsDirectory = layout.buildDirectory.dir("jacoco")
}

repositories {
	mavenCentral()
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/kaiqkt/*")
		credentials {
			username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
			password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_API_KEY")
		}
	}
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Custom Libraries
    implementation("com.kaiqkt:springtools-security:1.0.21")
    implementation("com.kaiqkt:springtools-healthcheck:1.0.0")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.3")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Other Libraries
    implementation("io.azam.ulidj:ulidj:1.0.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.flywaydb:flyway-core:11.1.0")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
    implementation("com.auth0:java-jwt:4.4.0")
	implementation("org.apache.commons:commons-text:1.10.0")

	// Test Dependencies
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("com.icegreen:greenmail-junit5:1.6.1")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

sourceSets {
	main {
		java {
			srcDir("$buildDir/generated/src/main/kotlin")
		}
	}
}

openApiGenerate {
	generatorName.set("kotlin-spring")
	skipValidateSpec.set(true)
	inputSpec.set("$rootDir/src/main/resources/static/api-docs.yml")
	outputDir.set("$buildDir/generated")
	apiPackage.set("com.trippy.auth.generated.application.web.controllers")
	modelPackage.set("com.trippy.auth.generated.application.web.dtos")
	configOptions.set(
		mapOf(
			"dateLibrary" to "java8",
			"interfaceOnly" to "true",
			"useBeanValidation" to "true",
			"enumPropertyNaming" to "UPPERCASE",
			"useSpringBoot3" to "true",
			"unhandledException" to "true"
		)
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val excludePackages: List<String> by extra {
	listOf(
		"com/trippy/auth/generated/*",
		"org/openapitools/*",
		"com/trippy/auth/Application*",
		"com/trippy/auth/application/config/*",
		"com/trippy/auth/application/web/requests/*",
		"com/trippy/auth/application/web/responses/*",
		"com/trippy/auth/domain/models/*",
		"com/trippy/auth/domain/utils/*",
		"com/trippy/auth/domain/dtos/*",
		"com/trippy/auth/resources/mail/MailGateway*",
	)
}

@Suppress("UNCHECKED_CAST")
fun ignorePackagesForReport(jacocoBase: JacocoReportBase) {
	jacocoBase.classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree.matching {
			exclude(jacocoBase.project.extra.get("excludePackages") as List<String>)
		}
	)
}

tasks.withType<JacocoReport> {
	reports {
		xml.required
		html.required
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
	ignorePackagesForReport(this)
}


tasks.withType<JacocoCoverageVerification> {
	violationRules {
		rule {
			limit {
				minimum = "1.0".toBigDecimal()
				counter = "LINE"
			}
			limit {
				minimum = "1.0".toBigDecimal()
				counter = "BRANCH"
			}
		}
	}
	ignorePackagesForReport(this)
}

tasks.compileKotlin {
	dependsOn(tasks.openApiGenerate)
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}