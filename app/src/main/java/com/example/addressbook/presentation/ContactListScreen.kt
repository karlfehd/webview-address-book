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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.addressbook.R
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
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ContactSearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                        onEvent(ContactEvent.SearchContacts(newQuery))
                    }
                )
            },
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
                            painter = painterResource(R.drawable.baseline_cloud_download_24),
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
                items(state.filteredContacts) { contact ->
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
            contacts = List(10) { randomContactGenerator() },
            isAddingContact = false,
            sortType = SortType.NAME,
            //successMessage = "Contacts imported successfully",
        )
        ContactListScreen(
            state = state,
            onEvent = {}
        )
    }

}

fun randomContactGenerator(): Contact {
    val companies =
        listOf("Acme Corp", "GlobalTech", "FoodCo", "TechStack", "DataSys", "SmartSolutions")
    val firstNames = listOf(
        "John",
        "Maria",
        "James",
        "Sarah",
        "Michael",
        "Emma",
        "David",
        "Anna",
        "Robert",
        "Lisa"
    )
    val lastNames = listOf(
        "Smith",
        "Johnson",
        "Brown",
        "Davis",
        "Miller",
        "Wilson",
        "Moore",
        "Taylor",
        "Anderson",
        "Thomas"
    )
    val titles = listOf(
        "CEO",
        "Manager",
        "Sales Representative",
        "Director",
        "Coordinator",
        "Supervisor",
        "Associate"
    )
    val cities = listOf(
        "New York",
        "London",
        "Berlin",
        "Paris",
        "Tokyo",
        "Sydney",
        "Toronto",
        "Madrid",
        "Rome",
        "Amsterdam"
    )
    val countries = listOf(
        "USA",
        "UK",
        "Germany",
        "France",
        "Japan",
        "Australia",
        "Canada",
        "Spain",
        "Italy",
        "Netherlands"
    )
    val domains = listOf("company.com", "corp.net", "business.org", "enterprise.com", "global.net")

    val firstName = firstNames.random()
    val lastName = lastNames.random()
    val company = companies.random()
    val domain = domains.random()
    val city = cities.random()
    val country = countries.random()

    return Contact(
        customerID = "${firstName.take(2)}${lastName.take(3)}".uppercase(),
        companyName = company,
        contactName = "$firstName $lastName",
        contactTitle = titles.random(),
        email = "${firstName.lowercase()}.${lastName.lowercase()}@$domain",
        phone = "${(100..999).random()}-${(100..999).random()}-${(1000..9999).random()}",
        address = "${(1..999).random()} ${('A'..'Z').random()} Street",
        city = city,
        country = country,
        postalCode = "${(10000..99999).random()}",
        fax = "${(100..999).random()}-${(100..999).random()}-${(1000..9999).random()}"
    )
}
