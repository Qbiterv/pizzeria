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
	}
}

project(":meal") {
	dependencies {
		implementation(group = "com.github.java-json-tools", name = "json-patch", version = "1.13")
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
