import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "ai.lail"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.bettercloud:vault-java-driver:5.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.fusionauth:fusionauth-jwt:5.2.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.mongock:mongock:5.2.4")
    implementation("io.mongock:mongock-springboot-v3:5.2.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.2.4")
    implementation("com.bucket4j:bucket4j-core:8.2.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude(module = "mockito-core") }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:4.0.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Allow access to BuildProperties Bean
springBoot {
    buildInfo()
}

// Extra custom tasks
tasks.register<Exec>("unwrap-win") {
    workingDir("./docker")
    executable("powershell")
    args("./vault/unwrap.sh")
}

tasks.register<Exec>("coldStart-win") {
    workingDir("./docker")
    executable("powershell")
    args("./start.sh")
}

tasks.register<Exec>("setup-win") {
    workingDir("./docker")
    executable("powershell")
    args("./setup.sh")
}

tasks.register<Exec>("unwrap-linux") {
    workingDir("./docker")
    executable("sh")
    args("-c", "./vault/unwrap.sh")
}

tasks.register<Exec>("coldStart-linux") {
    workingDir("./docker")
    executable("sh")
    args("-c", "./start.sh")
}

tasks.register<Exec>("setup-linux") {
    workingDir("./docker")
    executable("sh")
    args("-c", "./setup.sh")
}

configure(listOf(
    tasks["unwrap-win"], tasks["coldStart-win"], tasks["setup-win"],
    tasks["unwrap-linux"], tasks["coldStart-linux"], tasks["setup-linux"]
)) {
    group = "Pre-Run Scripts"
    description = "Responsible for bootstrapping Vault and containers prior to starting"
}