package com.imcys.bilibilias.shared.platform.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation._NSRange
import platform.UIKit.UIColor
import platform.UIKit.UIDataDetectorTypeLink
import platform.UIKit.UITextView
import platform.UIKit.UITextViewDelegateProtocol
import platform.UIKit.UITextItemInteraction
import platform.UIKit.labelColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ASHtmlText(
    html: String,
    modifier: Modifier,
    onLinkClick: ((url: String) -> Unit)?
) {
    val delegate = remember(onLinkClick) {
        object : NSObject(), UITextViewDelegateProtocol {
            override fun textView(
                textView: UITextView,
                shouldInteractWithURL: NSURL,
                inRange: CValue<_NSRange>,
                interaction: UITextItemInteraction
            ): Boolean {
                val urlString = shouldInteractWithURL.absoluteString ?: return true
                onLinkClick?.invoke(urlString)
                return onLinkClick == null
            }
        }
    }

    UIKitView(
        factory = {
            UITextView().apply {
                setEditable(false)
                setSelectable(true)
                setScrollEnabled(false)
                setDataDetectorTypes(UIDataDetectorTypeLink)
                setUserInteractionEnabled(true)
                setDelegate(delegate)
                setTextColor(UIColor.labelColor)
                setBackgroundColor(UIColor.clearColor)
            }
        },
        update = { textView ->
            textView.setText(html)
            textView.setDelegate(delegate)
        },
        modifier = modifier
    )
}
