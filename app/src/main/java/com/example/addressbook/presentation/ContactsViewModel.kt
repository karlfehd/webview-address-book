package com.example.addressbook.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.addressbook.domain.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val fileName = "contacts.json"
    private val file = File(application.filesDir, fileName)
    private val gson = Gson()

    init {
        loadFromFile()
    }

    fun addContact(contact: Contact) {
        _contacts.value = _contacts.value + contact
        saveToFile()
    }

    fun deleteContact(index: Int) {
        _contacts.value = _contacts.value.toMutableList().apply { removeAt(index) }
        saveToFile()
    }

    fun updateContact(index: Int, newContact: Contact) {
        _contacts.value = _contacts.value.toMutableList().apply { this[index] = newContact }
        saveToFile()
    }

    fun saveContactsFromJson(json: String) {
        val type = object : TypeToken<List<Contact>>() {}.type
        val parsed: List<Contact> = gson.fromJson(json, type)
        _contacts.value = parsed
        saveToFile()
    }

    fun getContactsAsJson(): String {
        return gson.toJson(_contacts.value)
    }

    private fun loadFromFile() {
        viewModelScope.launch {
            if (file.exists()) {
                val json = file.readText()
                saveContactsFromJson(json)
            }
        }
    }

    private fun saveToFile() {
        viewModelScope.launch {
            file.writeText(getContactsAsJson())
        }
    }
}

