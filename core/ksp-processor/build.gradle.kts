plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
}
