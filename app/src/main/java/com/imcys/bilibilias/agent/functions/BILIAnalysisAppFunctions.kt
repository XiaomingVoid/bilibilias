package com.imcys.bilibilias.agent.functions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.imcys.bilibilias.agent.functions.model.BILIUserProfile
import com.imcys.bilibilias.agent.functions.model.toUserProfile
import com.imcys.bilibilias.common.utils.AsRegexUtil
import com.imcys.bilibilias.common.utils.TextType
import com.imcys.bilibilias.data.repository.UserInfoRepository
import com.imcys.bilibilias.data.repository.VideoInfoRepository
import com.imcys.bilibilias.network.ApiStatus
import kotlinx.coroutines.flow.last

/**
 * 视频解析 AppFunctions
 */
class BILIAnalysisAppFunctions(
    private val videoInfoRepository: VideoInfoRepository,
    private val userInfoRepository: UserInfoRepository,
) {

    /**
     * 获取我的B站用户信息，需要登录。
     * @return 我的B站的信息。
     * @throws IllegalArgumentException 如果用户没有登录
     * @throws IllegalStateException 如果接口信息获取失败
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun getMeLoginInfo(functionContext: AppFunctionContext): BILIUserProfile {
        val user =
            userInfoRepository.getCurrentUser() ?: throw IllegalArgumentException("未找到登录信息")
        val result = userInfoRepository.getUserPageInfo(user.mid).last()
        return when (result.status) {
            ApiStatus.SUCCESS -> result.data?.toUserProfile() ?: throw IllegalStateException(result.errorMsg)
            ApiStatus.ERROR -> {
                throw IllegalStateException(result.errorMsg)
            }

            else -> throw IllegalStateException(result.errorMsg)
        }
    }

    /**
     * 获取B站用户的信息，无需登录。
     * @param mid 用户的UID/MID，纯数字。
     * @return 用户B站的信息，为null则代表没有查询到
     * @throws IllegalArgumentException 如果传入的MID不是数字
     * @throws IllegalStateException 如果没有找到用户信息
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun getUserPageInfo(functionContext: AppFunctionContext,mid: String): BILIUserProfile? {
        val uid = runCatching {
            mid.toLong()
        }.getOrNull() ?: throw IllegalArgumentException("mid格式错误")
        val result = userInfoRepository.getUserPageInfo(uid).last()
        return when (result.status) {
            ApiStatus.SUCCESS -> result.data?.toUserProfile()
            ApiStatus.ERROR -> {
                throw IllegalStateException(result.errorMsg)
            }

            else -> throw IllegalStateException(result.errorMsg)
        }
    }


    /**
     * 解析B站分享链接，返回视频、番剧、用户的BVID/EPID/SSID/UID
     * @param analysisText B站分享链接或含有B站视频ID的文本内容
     * @throws IllegalStateException 如果传入的是分享链接，且链接无法成功解析。
     * @throws IllegalArgumentException 如果不符合解析规则
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun analysisVideoOrDongHuaId(functionContext: AppFunctionContext,analysisText: String): String {
        return when (val asType = AsRegexUtil.parse(analysisText)) {
            is TextType.BILI.AV -> {
                "视频AV号：${asType.text}"
            }

            is TextType.BILI.BV -> {
                "视频BV号：${asType.text}"
            }

            is TextType.BILI.EP -> {
                "番剧EP号：${asType.text}"
            }

            is TextType.BILI.ShortLink -> {
                val result = runCatching { videoInfoRepository.shortLink(asType.text) }.getOrNull()
                    ?: throw IllegalStateException("无法正常解析")
                analysisVideoOrDongHuaId(functionContext,result)
            }
            is TextType.BILI.UserSpace -> {
                "用户UID：${asType.text}"
            }
            is TextType.BILI.SS -> {
                "番剧SSID：${asType.text}"
            }
            null -> {
                throw IllegalArgumentException("没有符合的解析规则，请检查是否是B站相关内容。")
            }

        }
    }

}