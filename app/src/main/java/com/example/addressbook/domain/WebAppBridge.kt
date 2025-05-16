package com.example.addressbook.domain

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.addressbook.presentation.ContactsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WebAppBridge(
    private val viewModel: ContactsViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val webViewProvider: WebView,
) {
    private val gson = Gson()

    init {
        // Observe contacts and push updates to WebView JS automatically
        lifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                val json = gson.toJson(contacts)
                val webView = webViewProvider
                webView.post {
                    webView.evaluateJavascript("loadContacts('$json');", null)
                }
            }
        }
    }

    @JavascriptInterface
    fun testBridge(message: String) {
        Log.d("WebAppBridge", "testBridge called with message: $message")
    }

    @JavascriptInterface
    fun importContacts(jsonContacts: String) {
        Log.d("WebAppBridge", "importContacts called with: $jsonContacts")
        viewModel.saveContactsFromJson(jsonContacts)
        webViewProvider.post {
            webViewProvider.evaluateJavascript("loadContacts('${viewModel.getContactsAsJson()}');", null)
        }
    }

    @JavascriptInterface
    fun createNewContact(jsonContact: String) {
        Log.d("WebAppBridge", "createNewContact called")
        val contact = gson.fromJson(jsonContact, Contact::class.java)
        viewModel.addContact(contact)
    }

    @JavascriptInterface
    fun exportContacts() {
        //viewModel.loadFromLocalFile()
    }

    @JavascriptInterface
    fun saveContacts(json: String) {
        viewModel.saveContactsFromJson(json)
    }
}