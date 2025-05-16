package com.example.addressbook.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.addressbook.domain.ContactEvent

@Composable
fun AddContactDialog(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(ContactEvent.HideDialog)
        },
        title = { Text(text = "Add Contact") },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(ContactEvent.SaveContact)
                }
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(ContactEvent.HideDialog)
                }
            ) {
                Text(text = "Cancel")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.contactName,
                    onValueChange = {
                        onEvent(ContactEvent.SetContactName(it))
                    },
                    placeholder = { Text(text = "Name") },
                )
                TextField(
                    value = state.contactTitle,
                    onValueChange = {
                        onEvent(ContactEvent.SetContactTitle(it))
                    },
                    placeholder = { Text(text = "Title") },
                )
                TextField(
                    value = state.contactEmail,
                    onValueChange = {
                        onEvent(ContactEvent.SetContactEmail(it))
                    },
                    placeholder = { Text(text = "Email") },
                )
                TextField(
                    value = state.contactPhone,
                    onValueChange = {
                        onEvent(ContactEvent.SetContactPhone(it))
                    },
                    placeholder = { Text(text = "Phone") },
                )
            }
        }
    )
}

