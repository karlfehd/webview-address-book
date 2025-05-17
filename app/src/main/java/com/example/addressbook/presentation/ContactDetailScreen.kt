package com.example.addressbook.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.addressbook.domain.Contact
import com.example.addressbook.ui.theme.AddressBookTheme

@Composable
fun ContactDetailsScreen(
    contact: Contact,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Text(
                text = "Contact Details",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Name", contact.contactName)
                DetailRow("Company", contact.companyName)
                DetailRow("Title", contact.contactTitle)
                DetailRow("Customer ID", contact.customerID)
                DetailRow("Email", contact.email)
                DetailRow("Phone", contact.phone)
                DetailRow("Address", contact.address)
                DetailRow("City", contact.city)
                DetailRow("Postal Code", contact.postalCode)
                DetailRow("Country", contact.country)
                DetailRow("Fax", contact.fax)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    if (value.isNotBlank()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ContactDetailsScreenPreview() {
    val contact = Contact(
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

    )
    AddressBookTheme {
        ContactDetailsScreen(
            contact = contact,
            onDismiss = {},
            onEdit = {}
        )
    }
}