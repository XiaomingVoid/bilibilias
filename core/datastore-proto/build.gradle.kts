plugins {
    alias(libs.plugins.bilibilias.jvm.library)
    alias(libs.plugins.protobuf)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                named("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
sourceSets {
    main {
        java {
            srcDir(provider { layout.buildDirectory.get().asFile.resolve("generated/source/proto/main/java") })
            srcDir(provider { layout.buildDirectory.get().asFile.resolve("generated/source/proto/main/kotlin") })
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}