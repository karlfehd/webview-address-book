package com.example.addressbook.presentation

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.example.addressbook.domain.WebAppBridge

@Composable
fun ContactsWebView(
    webView: WebView,
    viewModel: ViewModel
) {
    AndroidView(factory = {
        WebView(it).apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccessFromFileURLs= true
            settings.allowUniversalAccessFromFileURLs = true
            //webView = this
            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    webView.evaluateJavascript(
                        //"angular.element(document.body).scope().contacts = ${viewModel.getContactsAsJson()};"+
                                "angular.element(document.body).scope().\$apply();",
                        null
                    )
                }
            }
            webChromeClient = WebChromeClient()
            setWebContentsDebuggingEnabled(true)
            //addJavascriptInterface(WebAppBridge(viewModel, this@MainActivity, webView), "Android")
            loadUrl("file:///android_asset/index.html")
        }
    })
}