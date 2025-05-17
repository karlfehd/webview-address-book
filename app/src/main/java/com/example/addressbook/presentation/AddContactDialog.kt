package com.example.addressbook.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.addressbook.domain.ContactEvent
import com.example.addressbook.domain.SortType
import com.example.addressbook.ui.theme.AddressBookTheme

@Composable
fun AddContactDialog(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val isScrolledToBottom = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                false
            } else {
                val lastVisibleItem = visibleItems.last()
                lastVisibleItem.index == layoutInfo.totalItemsCount - 1 &&
                        lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
            }
        }
    }
    AlertDialog(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        onDismissRequest = {
            onEvent(ContactEvent.HideDialog)
        },
        title = {
            Text(
                text = if (state.isEditing) "Edit Contact" else "Add Contact",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(ContactEvent.SaveContact)
                },
                enabled = state.nameError.isEmpty() && state.emailError.isEmpty()
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.Top
                        ) {
                            ContactInputTextField(
                                value = state.contactName,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactName(it))
                                },
                                placeholder = "Name",
                                isRequired = true,
                                errorMessage = state.nameError
                            )
                            ContactInputTextField(
                                value = state.contactEmail,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactEmail(it))
                                },
                                placeholder = "Email",
                                isRequired = true,
                                errorMessage = state.emailError
                            )
                            ContactInputTextField(
                                value = state.contactPhone,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactPhone(it))
                                },
                                placeholder = "Phone"
                            )
                            ContactInputTextField(
                                value = state.customerID,
                                onValueChange = {
                                    onEvent(ContactEvent.SetCustomerId(it))
                                },
                                placeholder = "Customer ID"
                            )
                            ContactInputTextField(
                                value = state.companyName,
                                onValueChange = {
                                    onEvent(ContactEvent.SetCompanyName(it))
                                },
                                placeholder = "Company"
                            )
                            ContactInputTextField(
                                value = state.contactTitle,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactTitle(it))
                                },
                                placeholder = "Title"
                            )
                            ContactInputTextField(
                                value = state.contactAddress,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactAddress(it))
                                },
                                placeholder = "Street Address"
                            )
                            ContactInputTextField(
                                value = state.contactCity,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactCity(it))
                                },
                                placeholder = "City"
                            )
                            ContactInputTextField(
                                value = state.contactPostalCode,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactPostalCode(it))
                                },
                                placeholder = "State"
                            )
                            ContactInputTextField(
                                value = state.contactCountry,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactCountry(it))
                                },
                                placeholder = "Country"
                            )
                            ContactInputTextField(
                                value = state.contactFax,
                                onValueChange = {
                                    onEvent(ContactEvent.SetContactFax(it))
                                },
                                placeholder = "Fax number"
                            )
                        }
                    }
                }
                if (!isScrolledToBottom.value) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )
                }
            }
        }
    )
}

@Composable
private fun ContactInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isRequired: Boolean = false,
    errorMessage: String = ""
) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRequired) {
                Text(
                    text = "*",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            isError = errorMessage.isNotEmpty(),
            supportingText = if (errorMessage.isNotEmpty()) {
                { Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                ) }
            } else null,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorTextColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        )
    }
}

@PreviewLightDark
@Composable
fun AddContactDialogPreview() {
    AddressBookTheme {
        AddContactDialog(
            state = ContactState(
                contacts = emptyList(),
                isAddingContact = true,
                sortType = SortType.NAME,
            ),
            onEvent = {}
        )
    }
}
