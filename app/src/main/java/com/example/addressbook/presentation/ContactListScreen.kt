package com.example.addressbook.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var showImportDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = { showImportDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Import Contacts"
                        )
                    }
                    FloatingActionButton(
                        onClick = { onEvent(ContactEvent.ShowDialog) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact"
                        )
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) { padding ->
            if (showImportDialog) {
                ImportDialog(
                    onDismiss = { showImportDialog = false }
                )
            }
            if (state.isAddingContact) {
                AddContactDialog(
                    state = state,
                    onEvent = onEvent
                )
            }
            state.selectedContact?.let { contact ->
                ContactDetailsScreen(
                    contact = contact,
                    onDismiss = {
                        onEvent(ContactEvent.HideContactDetails)
                    },
                    onEdit = {
                        onEvent(ContactEvent.EditContact(contact))
                        onEvent(ContactEvent.HideContactDetails)
                    }
                )
            }
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SortType.entries.forEach { sortType ->
                            Row(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable {
                                        onEvent(ContactEvent.SortContacts(sortType))
                                    }
                                    .padding(horizontal = 4.dp),
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
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
                items(state.contacts) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                onEvent(ContactEvent.ShowContactDetails(contact))
                            }
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)

                        ) {
                            Text(
                                text = contact.contactName,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = contact.email,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = contact.phone,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }
                        IconButton(onClick = {
                            onEvent(ContactEvent.DeleteContact(contact))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Contact",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            state.successMessage?.let { message ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
            sortType = SortType.NAME,
            successMessage = "Contacts imported successfully",
        )
        ContactListScreen(
            state = state,
            onEvent = {}
        )
    }

}