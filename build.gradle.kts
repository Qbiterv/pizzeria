plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "pl.auctane"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

allprojects {
	repositories {
		mavenCentral()
	}

	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "java")
}

subprojects {
	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("org.springframework.boot:spring-boot-starter-jdbc")
		implementation("org.springframework.boot:spring-boot-starter-validation")
		implementation("org.springframework.boot:spring-boot-starter-web")
		compileOnly("org.projectlombok:lombok")
		runtimeOnly("org.postgresql:postgresql")
		annotationProcessor("org.projectlombok:lombok")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")

		compileOnly("com.rabbitmq:amqp-client:5.24.0")
	}

	tasks.register("dockerBuild") {
		group = "docker"
		description = "Builds a Docker image for this module"

		doLast {
			val moduleName = project.name
			exec {
				commandLine("docker", "build", "-t", "pizzeria-$moduleName:latest", ".")
			}
		}
	}

	tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
		archiveFileName.set("${project.name}.jar") // Ensures correct JAR naming
	}
}

project(":mail") {
	description = "Mail module"

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-mail")
		implementation("org.springframework:spring-context-support")
	}
}

project(":brandenburg") {
	description = "Gateway module, routes all the requests"

	extra["springCloudVersion"] = "2024.0.0"

	dependencies {
		implementation("org.springframework.cloud:spring-cloud-starter-gateway")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("io.projectreactor:reactor-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	dependencyManagement {
		imports {
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
		}
	}
}

tasks.register("buildAll") {
	group = "build"
	description = "Builds all subprojects"

	dependsOn(subprojects.map {
		it.tasks.named("build")
	})
}

tasks.withType<Test> {
	useJUnitPlatform()
}
