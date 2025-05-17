package com.example.addressbook.presentation

sealed class NavigationTarget(val route: String) {
    object Contacts : NavigationTarget("contacts")
    object WebView : NavigationTarget("webview")
}