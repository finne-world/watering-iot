plugins {
    application
    kotlin("jvm") version "1.5.20-M1"
}

application {
    // Define the main class for the application.
    mainClass.set("work.watering.iot.MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlinStdlibJdk8)
    implementation(aWSIoTDeviceSDK)
    implementation(jacksonModule)
    implementation(jacksonDatatypeJdk8)
    implementation(jacksonDatatypeJsr310)
    implementation(pi4j)
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "work.watering.iot.MainKt"
    }

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
