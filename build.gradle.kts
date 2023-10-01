plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
    id("com.diffplug.spotless") version "6.15.0"
}

group = "com.intellij.devtools"
version = "1.1-snapshot"
val channel: String = "snapshot"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    gradlePluginPortal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf(
            "com.intellij.properties",
            "org.jetbrains.plugins.yaml",
            "com.intellij.lang.jsgraphql:4.0.1"
    ))
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
}

dependencies {
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("pl.jalokim.propertiestojson:java-properties-to-json:5.3.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
    implementation("com.graphql-java:graphql-java:21.1")

}

dependencies {
    testImplementation("com.intellij.remoterobot:remote-robot:0.11.19")
    testImplementation("com.intellij.remoterobot:remote-fixtures:0.11.19")
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("com.automation-remarks:video-recorder-junit5:2.0")
}

dependencies {
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    initializeIntelliJPlugin {
        enabled = false
    }

    spotless {
        java {
            importOrder()
            removeUnusedImports()
            googleJavaFormat("1.17.0")
            formatAnnotations()
        }
    }

    runIdeForUiTests {
//        systemProperty("robot-server.port", "8082") // default port 8580
        systemProperty("ide.test.execution", "true");
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(channel))
    }

    test {
        // enable here nad in runIdeForUiTests block - to log the retrofit HTTP calls
        // systemProperty "debug-retrofit", "enable"

        // enable encryption on test side when use remote machine
        // systemProperty "robot.encryption.password", "my super secret"
        useJUnitPlatform()
    }
}