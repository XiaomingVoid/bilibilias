package com.imcys.bilibilias.network.adapter

import com.imcys.bilibilias.network.NetWorkResult
import com.imcys.bilibilias.network.model.BiliApiResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.FlowCollector

interface NetWorkAdapter<Data> {
    context(collector: FlowCollector<NetWorkResult<Data>>)
    suspend fun handleSuccess(
        data: Data?,
        apiResponse: BiliApiResponse<Data>,
        response: HttpResponse
    )

    context(response: HttpResponse)
    suspend fun <Data> conversion(
        data: Data?,
    ): BiliApiResponse<Data>
}
