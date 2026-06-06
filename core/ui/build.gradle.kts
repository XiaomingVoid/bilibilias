plugins {
    alias(libs.plugins.bilibilias.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.imcys.bilibilias.ui"
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)
            api(libs.compose.ui.tooling.preview)
            api(libs.androidx.material.icons.extended.kmp)
            api(libs.kmp.androidx.lifecycle.runtimeCompose)
            api(libs.kmp.androidx.lifecycle.viewmodel)
            api(libs.jetbrains.navigation3.ui)
            api(libs.jetbrains.material3.adaptive)
            api(libs.jetbrains.material3.adaptive.layout)
            api(libs.jetbrains.material3.adaptive.navigation)
            api(libs.coil.compose)
        }

        androidMain.dependencies {
            api(libs.coil.network.okhttp)
            api(libs.androidx.ui.tooling.preview)
        }

        iosMain.dependencies {
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.darwin)
        }
    }
}
