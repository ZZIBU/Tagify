plugins {
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("com.google.cloud.tools.jib") version "3.4.1"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "zzibu.jeho"
version = "0.0.1-SNAPSHOT"
val projectOwner : String by project

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://repo.spring.io/milestone")
		url = uri("https://repo.spring.io/snapshot")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// spring ai
	implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-SNAPSHOT")

	// kotlin-logging
	implementation("io.github.oshai:kotlin-logging-jvm:5.1.1")

	// PDFBox
	implementation("org.apache.pdfbox:pdfbox:2.0.24")

	// Tesseract (OCR Dependency)
	implementation("net.sourceforge.tess4j:tess4j:5.4.0")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// kotest
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
	testImplementation("io.kotest:kotest-assertions-core:5.4.2")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation(kotlin("stdlib-jdk8"))
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

// Jib 플러그인 설정
jib {
	from {
		image = "openjdk:21-jdk-slim"
	}
	to {
		image = "${projectOwner}/${project.name.lowercase()}"
		tags = setOf("$version", "latest")
	}
	container {
		labels = mapOf(
			"maintainer" to "Jeho Lee <jhl81094@gmail.com>",
			"org.opencontainers.image.title" to "tagify",
			"org.opencontainers.image.description" to "Tagging for all of your files",
			"org.opencontainers.image.version" to "$version",
			"org.opencontainers.image.authors" to "Jeho Lee <jhl81094@gmail.com>",
			"org.opencontainers.image.url" to "https://github.com/ZZIBU/Tagify"
		)
		jvmFlags = listOf(
			"-Xms512m",
			"-Xmx1024m"
		)
		ports = listOf("8080")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	exclude("**/Bean*Test.class", "**/*ControllerTest.class")
}
