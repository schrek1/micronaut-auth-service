val micronautVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.jetbrains.kotlin.plugin.jpa")
}

version = "0.1"
group = "cz.schrek.sherdog"

repositories {
    mavenCentral()
}

micronaut {
    version(micronautVersion)
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("cz.schrek.sherdog.*")
    }
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("org.mapstruct:mapstruct-processor:1.4.2.Final")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.configuration:micronaut-hibernate-validator:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")


    runtimeOnly("org.apache.logging.log4j:log4j-api:2.13.3")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    kaptTest("io.micronaut:micronaut-inject-java")
    testCompileOnly("io.micronaut:micronaut-inject")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut:micronaut-http-server-netty")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.assertj:assertj-core")
    testImplementation( "org.mockito:mockito-core:3.10.0")

}


application {
    mainClass.set("cz.schrek.sherdog.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

kapt {
    arguments {
        arg("micronaut.openapi.views.spec", "swagger-ui.enabled=true,swagger-ui.theme=flattop")
        arg("mapstruct.defaultComponentModel", "jsr330")
        arg("mapstruct.unmappedTargetPolicy", "WARN")
    }
}


tasks {

    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    test {
        useJUnitPlatform()
    }

}
