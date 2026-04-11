package com.imcys.bilibilias.agent.functions.model

import androidx.appfunctions.AppFunctionSerializable
import com.imcys.bilibilias.network.model.user.BILIUserSpaceAccInfo
import kotlinx.serialization.SerialName

/**
 * 简化版用户主页数据
 *
 * @param mid 用户ID
 * @param name 昵称
 * @param face 头像URL
 * @param level 等级
 * @param sign 签名
 * @param vip 会员信息
 */
@AppFunctionSerializable(isDescribedByKdoc = true)
data class BILIUserProfile(
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("face")
    val face: String,
    @SerialName("level")
    val level: Long,
    @SerialName("sign")
    val sign: String,
    @SerialName("vip")
    val vip: VipInfo?
) {
    /**
     * 简化版会员信息
     *
     * @param type 会员类型 (0: 无, 1: 月度, 2: 年度)
     * @param status 会员状态 (0: 无, 1: 有)
     * @param labelText 会员标签文字
     * @param nicknameColor 昵称颜色
     */
    @AppFunctionSerializable(isDescribedByKdoc = true)
    data class VipInfo(
        @SerialName("type")
        val type: Long,
        @SerialName("status")
        val status: Long,
        @SerialName("label_text")
        val labelText: String,
        @SerialName("nickname_color")
        val nicknameColor: String
    )
}

/**
 * 将完整版用户数据转换为简化版
 */
fun BILIUserSpaceAccInfo.toUserProfile(): BILIUserProfile {
    return BILIUserProfile(
        mid = this.mid,
        name = this.name,
        face = this.face,
        level = this.level,
        sign = this.sign,
        vip = this.vip?.let {
            BILIUserProfile.VipInfo(
                type = it.type,
                status = it.status,
                labelText = it.label.text,
                nicknameColor = it.nicknameColor
            )
        }
    )
}