plugins {
    alias(libs.plugins.bilibilias.multiplatform.library)
    alias(libs.plugins.bilibilias.multiplatform.koin)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kmp.nativecoroutines)
}

kotlin {
    android {
        namespace = "com.imcys.bilibilias.data"
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("src/main/java")
            dependencies {
                implementation(project(":core:common"))
                api(project(":core:database"))
                api(project(":core:datastore"))
                api(project(":core:network"))
                implementation(libs.kmp.nativecoroutines.annotations)
            }
        }
    }
}
