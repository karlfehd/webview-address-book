package com.example.addressbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.addressbook.presentation.ContactListScreen
import com.example.addressbook.presentation.ContactViewModel
import com.example.addressbook.presentation.WebViewModel
import com.example.addressbook.presentation.ContactsWebView
import com.example.addressbook.presentation.ContactsWebView2
import com.example.addressbook.presentation.NavigationTarget
import com.example.addressbook.ui.theme.AddressBookTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddressBookTheme {
                val navController = rememberNavController()
                var selectedRoute by remember { mutableStateOf(NavigationTarget.Contacts.route) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedRoute == NavigationTarget.Contacts.route,
                                onClick = {
                                    selectedRoute = NavigationTarget.Contacts.route
                                    navController.navigate(NavigationTarget.Contacts.route)
                                },
                                icon = { Icon(Icons.AutoMirrored.Filled.List, "Contacts") },
                                label = { Text("Contacts") }
                            )
                            NavigationBarItem(
                                selected = selectedRoute == NavigationTarget.WebView.route,
                                onClick = {
                                    selectedRoute = NavigationTarget.WebView.route
                                    navController.navigate(NavigationTarget.WebView.route)
                                },
                                icon = { Icon(Icons.Default.AccountBox, "Web View") },
                                label = { Text("Web View") }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationTarget.Contacts.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(NavigationTarget.Contacts.route) {
                            val viewModel: ContactViewModel by viewModels()
                            val state by viewModel.state.collectAsState()
                            ContactListScreen(
                                state = state,
                                onEvent = viewModel::onEvent
                            )
                        }
                        composable(NavigationTarget.WebView.route) {
                            val viewModel: WebViewModel by viewModels()
//                            ContactsWebView(
//                                webView = android.webkit.WebView(this@MainActivity),
//                                viewModel = viewModel
//                            )
                            ContactsWebView2(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}



