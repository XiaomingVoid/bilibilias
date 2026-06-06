package com.imcys.bilibilias.ksp.processor.koin

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KoinNativeExportProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KoinNativeExportProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}