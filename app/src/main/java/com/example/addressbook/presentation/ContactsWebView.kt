package com.example.addressbook.presentation

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.addressbook.domain.WebAppBridge


private const val URL = "file:///android_asset/index.html"

@Composable
fun ContactsWebView(
    webView: WebView,
    viewModel: WebViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = {
        WebView(it).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    webView.evaluateJavascript(
                        "angular.element(document.body).scope().contacts = ${viewModel.contacts};" +
                                "angular.element(document.body).scope().\$apply();",
                        null
                    )
                }
            }
            setWebContentsDebuggingEnabled(true)
            addJavascriptInterface(WebAppBridge(viewModel, lifecycleOwner, webView), "Android")
            loadUrl(URL)
        }
    })
}

@Composable
fun ContactsWebView2(
    viewModel: WebViewModel
) {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            addJavascriptInterface(WebAppBridge(viewModel, lifecycleOwner, this), "Android")
            loadUrl(URL)
        }
    }

    DisposableEffect(webView) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }
        onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    )
}