plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// Vert.x
 	implementation("io.vertx:vertx-core:4.4.5")
 	implementation("io.vertx:vertx-web:4.4.5")
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("cooperativepixelart.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
