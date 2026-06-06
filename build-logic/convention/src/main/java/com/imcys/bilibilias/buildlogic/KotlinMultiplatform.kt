/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imcys.bilibilias.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.Family

internal fun Project.configureKotlinMultiplatformAndroid(kotlinMultiplatformExtension: KotlinMultiplatformExtension) {
    kotlinMultiplatformExtension.apply {
        val iosXcframework = XCFramework(project.iosFrameworkBaseName())

        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
            compileSdk = libs.findVersion("androidCompileSdk").get().requiredVersion.toInt()
            minSdk = libs.findVersion("androidMinSdk").get().requiredVersion.toInt()
        }

        targets.withType(KotlinNativeTarget::class.java).configureEach {
            if (konanTarget.family == Family.IOS) {
                binaries.framework(project.iosFrameworkBaseName()) {
                    isStatic = true
                    iosXcframework.add(this)
                }
            }
        }

        val warningsAsErrors = providers.gradleProperty("warningsAsErrors").map {
            it.toBoolean()
        }.orElse(false)

        compilerOptions {
            allWarningsAsErrors.set(warningsAsErrors)
            freeCompilerArgs.add(
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
            freeCompilerArgs.add(
                /**
                 * Remove this args after Phase 3.
                 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-consistent-copy-visibility/#deprecation-timeline
                 */
                "-Xconsistent-data-class-copy-visibility",
            )
            freeCompilerArgs.add("-XXLanguage:+ContextParameters")
            freeCompilerArgs.add("-Xexplicit-backing-fields")
            freeCompilerArgs.add("-Xreturn-value-checker=check")
        }

        targets.configureEach {
            when (platformType) {
                KotlinPlatformType.jvm if this is KotlinJvmTarget -> {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }
                KotlinPlatformType.androidJvm if this is KotlinMultiplatformAndroidLibraryTarget -> {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }

                else -> {}
            }
        }
    }
}

private fun Project.iosFrameworkBaseName(): String = "AS" + path
    .removePrefix(":")
    .split(":")
    .joinToString(separator = "") { segment ->
        segment
            .replace("-", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString("") { word ->
                word.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            }
    }
