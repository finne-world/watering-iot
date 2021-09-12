plugins {
    kotlin("jvm") version "1.5.20-M1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlinStdlibJdk8)
    implementation(AWSIoTDeviceSDK)
    implementation(JacksonModule)
}
