import com.imcys.bilibilias.buildlogic.configureKotlinMultiplatformAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.kotlin.multiplatform.library")
            }
            extensions.configure<KotlinMultiplatformExtension> {
                configureKotlinMultiplatformAndroid(this)
            }
        }
    }
}
