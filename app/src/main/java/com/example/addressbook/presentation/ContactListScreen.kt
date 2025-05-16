package com.example.addressbook.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.addressbook.domain.Contact
import com.example.addressbook.domain.ContactEvent
import com.example.addressbook.domain.SortType
import com.example.addressbook.ui.theme.AddressBookTheme

@Composable
fun ContactListScreen(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(ContactEvent.ShowDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact"
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    ) { padding ->
        if (state.isAddingContact) {
            AddContactDialog(
                state = state,
                onEvent = onEvent
            )
        }
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SortType.entries.forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(ContactEvent.SortContacts(sortType))
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(ContactEvent.SortContacts(sortType))
                                }
                            )
                            Text(
                                text = sortType.name.lowercase()
                                    .replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
            items(state.contacts) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .background(MaterialTheme.colorScheme.onPrimary)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = contact.contactName,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = contact.email,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = contact.phone,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelMedium
                        )

                    }
                    IconButton(onClick = {
                        onEvent(ContactEvent.DeleteContact(contact))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Contact",
                            tint = MaterialTheme.colorScheme.surfaceTint
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ContactScreenPreview() {
    AddressBookTheme {
        val state = ContactState(
            contacts = listOf(
                Contact(
                    customerID = "ALFKI",
                    companyName = "Alfreds Futterkiste",
                    contactName = "Maria Anders",
                    contactTitle = "Sales Representative",
                    email = "test@test.com",
                    phone = "030-0074321",
                    address = "Obere Str. 57",
                    city = "Berlin",
                    country = "Germany",
                    postalCode = "12209",
                    fax = "030-0076545"
                ),
                Contact(
                    customerID = "ALFD",
                    companyName = "McDonald's",
                    contactName = "John Doe",
                    contactTitle = "Manager",
                    email = "jdoe@mcd.com",
                    phone = "123-456-7890",
                    address = "123 Fast Food St.",
                    city = "Chicago",
                    country = "USA",
                    postalCode = "60601",
                    fax = "123-456-7891"
                )
            ),
            isAddingContact = false,
            sortType = SortType.NAME
        )
        ContactListScreen(
            state = state,
            onEvent = {}
        )
    }
}