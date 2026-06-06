plugins {
    alias(libs.plugins.bilibilias.multiplatform.library)
    alias(libs.plugins.bilibilias.multiplatform.koin)
}

kotlin {
    android {
        namespace = "com.imcys.bilibilias.datastore"
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.datastore.core)
            api(libs.androidx.datastore.core.okio)
            api(project(":core:datastore-proto"))
        }
    }
}
