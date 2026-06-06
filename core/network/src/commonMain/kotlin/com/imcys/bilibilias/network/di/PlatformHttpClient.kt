package com.imcys.bilibilias.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect fun platformHttpClient(
    block: HttpClientConfig<*>.() -> Unit
): HttpClient
