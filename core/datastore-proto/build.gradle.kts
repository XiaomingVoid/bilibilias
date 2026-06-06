import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.wire)
}

kotlin {
    val iosXcframework = XCFramework("ASCoreDatastoreProto")

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosArm64 {
        binaries.framework("ASCoreDatastoreProto") {
            isStatic = true
            iosXcframework.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework("ASCoreDatastoreProto") {
            isStatic = true
            iosXcframework.add(this)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.wire.runtime)
        }
    }
}
// Proto
wire {
    kotlin {}
}
