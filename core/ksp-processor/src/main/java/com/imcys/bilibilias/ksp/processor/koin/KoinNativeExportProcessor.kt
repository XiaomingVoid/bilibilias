package com.imcys.bilibilias.ksp.processor.koin

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.imcys.bilibilias.common.annotation.KoinNativeExport
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

class KoinNativeExportProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val directSymbols = resolver.getSymbolsWithAnnotation(KoinNativeExport::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .toList()

        val discoveredClassNames = discoverAnnotatedClassNames()

        val resolvedDependencySymbols = discoveredClassNames
            .mapNotNull { qualifiedName ->
                resolver.getClassDeclarationByName(resolver.getKSNameFromString(qualifiedName))
                    ?: run {
                        logger.warn("Unable to resolve @KoinNativeExport class on current classpath: $qualifiedName")
                        null
                    }
            }

        val symbols = (directSymbols + resolvedDependencySymbols)
            .distinctBy { it.qualifiedName?.asString().orEmpty() }
            .filter { it.qualifiedName != null }
            .sortedBy { it.qualifiedName!!.asString() }

        if (symbols.isEmpty()) return emptyList()

        val injectMember = MemberName("org.koin.core.component", "inject")
        val koinComponent = ClassName("org.koin.core.component", "KoinComponent")
        val sourceFiles = symbols.mapNotNull { it.containingFile }.distinct().toTypedArray()

        val dataProvider = TypeSpec.objectBuilder("DataProvider")
            .addSuperinterface(koinComponent)
            .apply {
                symbols
                    .forEach { clazz ->
                        val qualifiedName = clazz.qualifiedName?.asString()
                        if (qualifiedName.isNullOrBlank()) {
                            logger.warn("Skip @KoinNativeExport on local or anonymous class: ${clazz.simpleName.asString()}")
                            return@forEach
                        }
                        addProperty(
                            PropertySpec.builder(
                                clazz.simpleName.asString().toExportPropertyName(),
                                ClassName.bestGuess(qualifiedName)
                            )
                                .addModifiers(KModifier.PUBLIC)
                                .delegate(CodeBlock.of("%M()", injectMember))
                                .build()
                        )
                    }
            }
            .build()

        val packageName = "com.imcys.bilibilias.shared"
        val fileName = "DataProvider"
        val fileSpec = FileSpec.builder(packageName, fileName)
            .addType(dataProvider)
            .build()

        fileSpec.write(aggregating = true, files = sourceFiles)

        generated = true
        return emptyList()
    }

    private fun discoverAnnotatedClassNames(): Set<String> {
        val projectRoot = locateProjectRoot() ?: run {
            logger.warn("Unable to locate project root for KoinNativeExport scan.")
            return emptySet()
        }

        return projectRoot
            .walkTopDown()
            .onEnter { dir -> dir.name != "build" && dir.name != ".gradle" && !dir.name.startsWith(".git") }
            .filter { file -> file.isFile && (file.extension == "kt" || file.extension == "java") }
            .mapNotNull(::extractAnnotatedClassName)
            .toSet()
    }

    private fun locateProjectRoot(): File? {
        var current = File(System.getProperty("user.dir")).absoluteFile
        repeat(10) {
            if (File(current, "settings.gradle.kts").exists() || File(current, "settings.gradle").exists()) {
                return current
            }
            current.parentFile?.let { parent -> current = parent } ?: return null
        }
        return null
    }

    private fun extractAnnotatedClassName(file: File): String? {
        val lines = file.readLines()
        var packageName = ""
        var annotationPending = false

        for (rawLine in lines) {
            val line = rawLine.substringBefore("//").trim()
            if (line.isEmpty()) continue

            if (line.startsWith("package ")) {
                packageName = line.removePrefix("package ").trim()
                continue
            }

            if (line.contains("@KoinNativeExport") ||
                line.contains("@com.imcys.bilibilias.common.annotation.KoinNativeExport")
            ) {
                annotationPending = true
                continue
            }

            if (!annotationPending) continue

            if (line.startsWith("@")) continue

            val match = CLASS_DECLARATION_REGEX.find(line)
            if (match != null) {
                val simpleName = match.groupValues[2]
                return if (packageName.isBlank()) simpleName else "$packageName.$simpleName"
            }

            if (!line.startsWith("expect ") &&
                !line.startsWith("actual ") &&
                !line.startsWith("internal ") &&
                !line.startsWith("public ") &&
                !line.startsWith("private ") &&
                !line.startsWith("protected ") &&
                !line.startsWith("sealed ") &&
                !line.startsWith("data ") &&
                !line.startsWith("abstract ") &&
                !line.startsWith("open ")
            ) {
                annotationPending = false
            }
        }

        return null
    }

    private companion object {
        val CLASS_DECLARATION_REGEX = Regex("""\b(class|interface|object)\s+([A-Za-z_][A-Za-z0-9_]*)\b""")
    }

    private fun FileSpec.write(
        aggregating: Boolean = false,
        vararg files: KSFile
    ) {
        val dependencies = Dependencies(aggregating = aggregating, sources = files)
        val outputStream = codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = name
        )
        outputStream.writer().use(::writeTo)
    }
}

private fun String.toExportPropertyName(): String {
    if (isEmpty()) return this
    if (length == 1) return lowercase()

    val boundary = indexOfFirst { it.isLowerCase() }
    return when {
        boundary <= 0 -> replaceFirstChar(Char::lowercase)
        boundary == 1 -> replaceFirstChar(Char::lowercase)
        else -> substring(0, boundary - 1).lowercase() + substring(boundary - 1)
    }
}
