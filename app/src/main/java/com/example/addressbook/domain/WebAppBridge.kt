package com.example.addressbook.domain

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.addressbook.presentation.WebViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WebAppBridge(
    private val viewModel: WebViewModel,
    lifecycleOwner: LifecycleOwner,
    private val webViewProvider: WebView,
) {
    private val gson = Gson()

    init {
        // Observe contacts and push updates to WebView JS automatically
        lifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                val json = gson.toJson(contacts)
                Log.d("WebAppBridge", "Contacts from init: $json")
                webViewProvider.post {
                    webViewProvider.evaluateJavascript(
                        "(function() {" +
                                "var scope = angular.element(document.body).scope();" +
                                "scope.contacts = $json;" +
                                "scope.\$apply();" +
                                "})();",
                        null
                    )
                }
            }
        }
    }

    @JavascriptInterface
    fun testBridge(message: String) {
        Log.d("WebAppBridge", "testBridge called with message: $message")
        Log.d("WebAppBridge", "Contacts: ${viewModel.getContactsAsJson()}")
    }

    @JavascriptInterface
    fun createNewContact(jsonContact: String) {
        Log.d("WebAppBridge", "createNewContact called with: $jsonContact")
        try {
            val contact = gson.fromJson(jsonContact, Contact::class.java)
            viewModel.addContact(contact)
        } catch (e: Exception) {
            Log.e("WebAppBridge", "Error creating contact: ${e.message}")
        }
    }

    @JavascriptInterface
    fun deleteContact(index: Int) {
        Log.d("WebAppBridge", "deleteContact called for index: $index")
        viewModel.deleteContact(index)
    }

    @JavascriptInterface
    fun updateContact(jsonContact: String) {
        Log.d("WebAppBridge", "updateContact called with: $jsonContact")
        try {
            val contact = gson.fromJson(jsonContact, Contact::class.java)
            viewModel.updateContact(contact)
        } catch (e: Exception) {
            Log.e("WebAppBridge", "Error updating contact: ${e.message}")
        }
    }

}