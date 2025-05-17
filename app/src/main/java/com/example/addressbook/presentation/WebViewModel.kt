package com.example.addressbook.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.addressbook.data.ContactDao
import com.example.addressbook.domain.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val dao: ContactDao
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getContactsSortedByName().collect { contacts ->
                _contacts.value = contacts
            }
        }
    }

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            dao.insertContact(contact)
        }
    }

    fun deleteContact(index: Int) {
        viewModelScope.launch {
            dao.deleteContact(_contacts.value[index])
        }
    }

    fun updateContact(newContact: Contact) {
        viewModelScope.launch {
            dao.insertContact(newContact)
        }
    }

    fun getContactsAsJson(): String {
        return _contacts.value.toString()
    }

}

