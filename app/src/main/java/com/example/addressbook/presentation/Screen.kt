package com.example.addressbook.presentation

sealed class Screen(val route: String) {
    object Contacts : Screen("contacts")
    object WebView : Screen("webview")
}