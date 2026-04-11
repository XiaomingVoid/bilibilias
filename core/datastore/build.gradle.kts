plugins {
    alias(libs.plugins.bilibilias.android.library)
    alias(libs.plugins.bilibilias.android.koin)
}


android {
    namespace = "com.imcys.bilibilias.datastore"
}


dependencies {
    api(libs.protobuf.kotlin.lite)
    api(libs.androidx.datastore)
    api(libs.androidx.datastore.core)
    api(project(":core:datastore-proto"))
}