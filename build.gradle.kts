import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
    repositories {
        jcenter()
        gradlePluginPortal()
    }
    extra["nextVersion"] = "major"
}

plugins {
    `java-gradle-plugin`
    groovy
    `kotlin-dsl`
    idea
    jacoco
    id("com.gradle.plugin-publish") version "0.11.0"
    id("com.cinnober.gradle.semver-git") version "3.0.0"
}

group = "com.github.node-gradle"

val compatibilityVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = compatibilityVersion
    targetCompatibility = compatibilityVersion
}

repositories {
    jcenter()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.6.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("cglib:cglib-nodep:3.2.9")
    testImplementation("org.objenesis:objenesis:3.1")
    testImplementation("org.apache.commons:commons-io:1.3.2")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("com.github.stefanbirkner:system-rules:1.19.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    if (project.hasProperty("skipIT")) {
        exclude("**/*_integTest*")
    }
    if (project.hasProperty("testAllSupportedGradleVersions")) {
        systemProperty("testAllSupportedGradleVersions", "true")
    }
    val processorsCount = Runtime.getRuntime().availableProcessors()
    maxParallelForks = if (processorsCount > 2) processorsCount.div(2) else processorsCount
    testLogging {
        events = setOf(TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

gradlePlugin {
    plugins {
        create("nodePlugin") {
            id = "com.github.node-gradle.node"
            implementationClass = "com.github.gradle.node.NodePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/node-gradle/gradle-node-plugin"
    vcsUrl = "https://github.com/node-gradle/gradle-node-plugin"

    (plugins) {
        "nodePlugin" {
            id = "com.github.node-gradle.node"
            displayName = "Gradle Node.js Plugin"
            description = "Gradle plugin for executing Node.js scripts. Supports npm and Yarn."
            tags = listOf("java", "gradle", "node", "node.js", "npm", "yarn")
        }
    }
}
