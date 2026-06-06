package com.imcys.bilibilias.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource

/**
 * 序列化
 */
object AppSettingsSerializer : OkioSerializer<AppSettings> {

    val appSettingsDefault = AppSettings(
        video_naming_rule = "{p_title}",
        bangumi_naming_rule = "{episode_title}",
        use_tool_history = listOf("WebParser", "FrameExtractor"),
        enabled_clipboard_auto_handling = true,
        video_parse_platform = AppSettings.VideoParsePlatform.Web,
        use_video_container = "mp4",
        use_audio_container = "m4a",
        max_concurrent_downloads = 1,
        enabled_concurrent_merge = false,
        enabled_nav_on_back_invoked_callback = true,
        enabled_nav_animation = true,
    )

    override val defaultValue: AppSettings = appSettingsDefault

    override suspend fun readFrom(source: BufferedSource): AppSettings {
        try {
            val parsed = AppSettings.ADAPTER.decode(source)
            var modified = false
            val validVideoContainers = setOf("mp4", "mkv")
            val validAudioContainers = setOf("m4a", "mp3")
            val sanitized = parsed.copy(
                bangumi_naming_rule = parsed.bangumiNamingRule.ifBlank { defaultValue.bangumiNamingRule },
                video_naming_rule = parsed.videoNamingRule.ifBlank { defaultValue.videoNamingRule },
                use_tool_history = if (parsed.useToolHistoryList.isEmpty()) defaultValue.useToolHistoryList else parsed.use_tool_history,
                enabled_clipboard_auto_handling = if (parsed.hasEnabledClipboardAutoHandling()) parsed.enabled_clipboard_auto_handling else defaultValue.enabledClipboardAutoHandling,
                video_parse_platform = if (parsed.hasVideoParsePlatform()) parsed.video_parse_platform else defaultValue.videoParsePlatform,
                use_video_container = if (parsed.useVideoContainer.isEmpty() || parsed.useVideoContainer !in validVideoContainers) defaultValue.useVideoContainer else parsed.use_video_container,
                use_audio_container = if (parsed.useAudioContainer.isEmpty() || parsed.useAudioContainer !in validAudioContainers) defaultValue.useAudioContainer else parsed.use_audio_container,
                max_concurrent_downloads = if (parsed.maxConcurrentDownloads <= 0) defaultValue.maxConcurrentDownloads else parsed.max_concurrent_downloads,
                enabled_concurrent_merge = if (parsed.maxConcurrentDownloads <= 1 && parsed.enabledConcurrentMerge) false else parsed.enabled_concurrent_merge,
                enabled_nav_on_back_invoked_callback = if (parsed.hasEnabledNavOnBackInvokedCallback()) parsed.enabled_nav_on_back_invoked_callback else defaultValue.enabledNavOnBackInvokedCallback,
                enabled_nav_animation = if (parsed.hasEnabledNavAnimation()) parsed.enabled_nav_animation else defaultValue.enabledNavAnimation,
            )
            modified = sanitized != parsed
            return if (modified) sanitized else parsed
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: AppSettings, sink: BufferedSink) {
        AppSettings.ADAPTER.encode(sink, t)
    }
}
