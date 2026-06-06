package com.imcys.bilibilias.shared.platform.component

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PlatformWebView(
    modifier: Modifier,
    currentUrl: String,
    onUpdateUrl: (String) -> Unit,
    onProgressChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowFileAccess = false

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setWebContentsDebuggingEnabled(true)
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChanged(newProgress)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    handleRouterChange(view)
                    handleHomePageTipButton(view)
                }
            }
            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun onRouteChange(url: String?) {
                    onUpdateUrl(url ?: "")
                }

                @JavascriptInterface
                fun onUniversalLink(link: String) {
                    loadUrl(link)
                }
            }, "AndroidBridge")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.stopLoading()
            webView.removeAllViews()
            webView.destroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { webView },
        update = { view ->
            if (view.url != currentUrl) {
                view.loadUrl(currentUrl)
            }
        }
    )
}

private fun handleHomePageTipButton(view: WebView?) {
    val js =
        """
            var btns = document.querySelectorAll('.v5-button.m-fixed-openapp.v5-button--large.v5-button--primary.v5-button--block');
            btns.forEach(function(btn){
                btn.remove();
            });
        """.trimIndent()
    view?.evaluateJavascript(js, null)
}

private fun handleRouterChange(view: WebView?) {
    val routeJs = """
            (function() {
                function notifyJava(route){
                    if(window.AndroidBridge && window.AndroidBridge.onRouteChange){
                        window.AndroidBridge.onRouteChange(route);
                    }
                }
                window.addEventListener('popstate', function() {
                    notifyJava(window.location.href);
                });
                window.addEventListener('hashchange', function() {
                    notifyJava(window.location.href);
                });
                var pushState = history.pushState;
                var replaceState = history.replaceState;
                history.pushState = function(){
                    pushState.apply(history, arguments);
                    notifyJava(window.location.href);
                }
                history.replaceState = function(){
                    replaceState.apply(history, arguments);
                    notifyJava(window.location.href);
                }
            })();
    """
    view?.evaluateJavascript(routeJs, null)
}
