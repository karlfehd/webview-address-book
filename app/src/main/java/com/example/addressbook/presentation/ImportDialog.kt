package com.example.addressbook.presentation


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.addressbook.domain.ContactEvent
import com.example.addressbook.util.ImportResult
import com.example.addressbook.util.XmlParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ImportDialog(
    onDismiss: () -> Unit
) {
    val viewModel: ContactViewModel = hiltViewModel()
    var urlText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val handleFileContent: (String) -> Unit = { content ->
        try {
            when (val result = XmlParser.parseContactsXml(content)) {
                is ImportResult.Success -> {
                    result.contacts.forEach { contact ->
                        viewModel.onEvent(ContactEvent.SaveContact(contact))
                    }
                    viewModel.onEvent(ContactEvent.ImportSuccess(result.count))
                    onDismiss()
                }

                ImportResult.EmptyFile -> {
                    errorMessage = "The file is empty or contains no valid contacts"
                }

                ImportResult.InvalidFormat -> {
                    errorMessage = "Invalid XML format. Please check the file structure"
                }

                is ImportResult.Error -> {
                    errorMessage = "Error: ${result.message}"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error parsing file: ${e.localizedMessage}"
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val content = inputStream?.bufferedReader()?.use { it.readText() }
                if (content != null) {
                    handleFileContent(content)
                } else {
                    errorMessage = "Unable to read file content"
                }
            } catch (e: Exception) {
                errorMessage = "Failed to read file: ${e.localizedMessage}"
            }
        }
    }

    val handleUrlImport = {
        if (urlText.isNotBlank()) {
            isLoading = true
            errorMessage = null
            scope.launch(Dispatchers.IO) {
                try {
                    val response = URL(urlText).readText()
                    withContext(Dispatchers.Main) {
                        handleFileContent(response)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Failed to fetch URL: ${e.localizedMessage}"
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    }

    AlertDialog(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Import Contacts",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = { filePickerLauncher.launch("text/xml") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Select Local File")
                }

                Text(
                    text = "OR",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                TextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter URL") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            successMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = handleUrlImport,
                enabled = urlText.isNotBlank() && !isLoading
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}