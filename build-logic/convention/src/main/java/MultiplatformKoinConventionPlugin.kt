import com.imcys.bilibilias.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class MultiplatformKoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("commonMainImplementation", platform(libs.findLibrary("koin-bom").get()))
                add("commonMainImplementation", libs.findLibrary("koin-core").get())
            }
        }
    }
}
