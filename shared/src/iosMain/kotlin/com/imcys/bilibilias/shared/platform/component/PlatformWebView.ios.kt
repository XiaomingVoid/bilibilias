package com.imcys.bilibilias.shared.platform.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebView
import platform.darwin.NSObject

private const val RouteMessageHandlerName = "bilibiliasRoute"

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(
    modifier: Modifier,
    currentUrl: String,
    onUpdateUrl: (String) -> Unit,
    onProgressChanged: (Int) -> Unit
) {
    val webViewUrl = remember { mutableStateOf(currentUrl) }
    val onUpdateRoute: (String) -> Unit = remember(onUpdateUrl) {
        { url ->
            if (url.isNotBlank()) {
                webViewUrl.value = url
                onUpdateUrl(url)
            }
        }
    }
    val navigationDelegate = remember(onUpdateRoute) {
        object : NSObject(), WKNavigationDelegateProtocol, WKScriptMessageHandlerProtocol {
            override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                webView.evaluateJavaScript(
                    javaScriptString = "window.location.href",
                    completionHandler = { result, _ ->
                        (result as? String)?.let(onUpdateRoute)
                    }
                )
                webView.evaluateJavaScript(
                    javaScriptString = removeHomePageTipButtonScript(),
                    completionHandler = null
                )
            }

            override fun userContentController(
                userContentController: WKUserContentController,
                didReceiveScriptMessage: WKScriptMessage
            ) {
                (didReceiveScriptMessage.body as? String)?.let(onUpdateRoute)
            }
        }
    }

    UIKitView(
        factory = {
            WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = webViewConfiguration(navigationDelegate)).apply {
                setNavigationDelegate(navigationDelegate)
                loadUrl(currentUrl)
            }
        },
        update = { webView ->
            webView.setNavigationDelegate(navigationDelegate)
            if (currentUrl != webViewUrl.value) {
                webViewUrl.value = currentUrl
                webView.loadUrl(currentUrl)
            }
        },
        modifier = modifier
    )
}

private fun WKWebView.loadUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    loadRequest(NSURLRequest.requestWithURL(nsUrl))
}

@OptIn(ExperimentalForeignApi::class)
private fun webViewConfiguration(messageHandler: WKScriptMessageHandlerProtocol): WKWebViewConfiguration {
    val userContentController = WKUserContentController()
    userContentController.addUserScript(
        WKUserScript(
            source = routeChangeScript(),
            injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentEnd,
            forMainFrameOnly = false
        )
    )
    userContentController.addScriptMessageHandler(messageHandler, RouteMessageHandlerName)

    return WKWebViewConfiguration().apply {
        this.userContentController = userContentController
    }
}

private fun routeChangeScript(): String =
    """
        (function() {
            if (window.__BILIBILIAS_ROUTE_HOOKED__) return;
            window.__BILIBILIAS_ROUTE_HOOKED__ = true;
            function notifyRoute() {
                try {
                    window.webkit.messageHandlers.$RouteMessageHandlerName.postMessage(window.location.href);
                } catch (e) {}
            }
            window.addEventListener('popstate', notifyRoute);
            window.addEventListener('hashchange', notifyRoute);
            var pushState = history.pushState;
            var replaceState = history.replaceState;
            history.pushState = function() {
                pushState.apply(history, arguments);
                notifyRoute();
            };
            history.replaceState = function() {
                replaceState.apply(history, arguments);
                notifyRoute();
            };
            document.addEventListener('click', function() {
                setTimeout(notifyRoute, 300);
            }, true);
            notifyRoute();
        })();
    """.trimIndent()

private fun removeHomePageTipButtonScript(): String =
    """
        var btns = document.querySelectorAll('.v5-button.m-fixed-openapp.v5-button--large.v5-button--primary.v5-button--block');
        btns.forEach(function(btn){
            btn.remove();
        });
    """.trimIndent()
