package com.example.addressbook.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.addressbook.data.ContactDao
import com.example.addressbook.domain.Contact
import com.example.addressbook.domain.ContactEvent
import com.example.addressbook.domain.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ContactViewModel @Inject constructor(
    private val dao: ContactDao
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.NAME)

    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.NAME -> dao.getContactsSortedByName()
                SortType.EMAIL -> dao.getContactsSortedByEmail()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ContactState())

    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType,
            filteredContacts = filterContacts(contacts, state.searchQuery)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContactState())

    private fun filterContacts(contacts: List<Contact>, query: String): List<Contact> {
        return if (query.isBlank()) {
            contacts
        } else {
            contacts.filter { contact ->
                contact.contactName.contains(query, ignoreCase = true) ||
                        //contact.companyName.contains(query, ignoreCase = true) ||
                        contact.email.contains(query, ignoreCase = true)
            }
        }
    }

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.SearchContacts -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        filteredContacts = filterContacts(_contacts.value, event.query)
                    )
                }
            }

            is ContactEvent.EditContact -> {
                _state.update {
                    it.copy(
                        isAddingContact = true,
                        isEditing = true,
                        editingContact = event.contact,
                        customerID = event.contact.customerID,
                        companyName = event.contact.companyName,
                        contactName = event.contact.contactName,
                        contactTitle = event.contact.contactTitle,
                        contactEmail = event.contact.email,
                        contactPhone = event.contact.phone,
                        contactAddress = event.contact.address,
                        contactCity = event.contact.city,
                        contactCountry = event.contact.country,
                        contactPostalCode = event.contact.postalCode,
                        contactFax = event.contact.fax
                    )
                }
            }

            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }

            ContactEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = false,
                        isEditing = false,
                        editingContact = null,
                        customerID = "",
                        companyName = "",
                        contactName = "",
                        contactTitle = "",
                        contactEmail = "",
                        contactPhone = "",
                        contactAddress = "",
                        contactCity = "",
                        contactCountry = "",
                        contactPostalCode = "",
                        contactFax = ""

                    )
                }
            }

            is ContactEvent.SaveContact -> {
                if (event.contact != null) {
                    viewModelScope.launch {
                        if (!isDuplicateContact(event.contact.contactName)) {
                            dao.insertContact(event.contact)
                        }
                    }
                } else {
                    val contact = Contact(
                        customerID = state.value.customerID,
                        companyName = state.value.companyName,
                        contactName = state.value.contactName,
                        contactTitle = state.value.contactTitle,
                        email = state.value.contactEmail,
                        phone = state.value.contactPhone,
                        address = state.value.contactAddress,
                        city = state.value.contactCity,
                        country = state.value.contactCountry,
                        postalCode = state.value.contactPostalCode,
                        fax = state.value.contactFax
                    )
                    if (!validateContact()) {
                        return
                    }
                    viewModelScope.launch {
                        if (isDuplicateContact(contact.contactName)) {
                            _state.update {
                                it.copy(
                                    errorMessage = "A contact with this name already exists"
                                )
                            }
                            return@launch
                        }
                        if (state.value.isEditing) {
                            state.value.editingContact?.let { oldContact ->
                                dao.deleteContact(oldContact)
                            }
                        }
                        dao.insertContact(contact)
                        _state.update {
                            it.copy(
                                isAddingContact = false,
                                customerID = "",
                                companyName = "",
                                contactName = "",
                                contactTitle = "",
                                contactEmail = "",
                                contactPhone = "",
                                contactAddress = "",
                                contactCity = "",
                                contactCountry = "",
                                contactPostalCode = "",
                                contactFax = ""
                            )
                        }
                    }
                }


            }

            is ContactEvent.ImportSuccess -> {
                _state.update { it.copy(successMessage = "Successfully imported ${event.count} contacts") }
                // Clear success message after delay
                viewModelScope.launch {
                    delay(3000)
                    _state.update { it.copy(successMessage = null) }
                }
            }

            is ContactEvent.SetContactEmail -> {
                _state.update {
                    it.copy(
                        contactEmail = event.email,
                        emailError = if (event.email.isNotBlank() && isValidEmail(event.email)) {
                            ""
                        } else it.emailError
                    )
                }
            }

            is ContactEvent.SetContactName -> {
                _state.update {
                    it.copy(
                        contactName = event.name,
                        nameError = if (event.name.isBlank()) "" else it.nameError
                    )
                }
            }

            is ContactEvent.SetContactPhone -> {
                _state.update {
                    it.copy(
                        contactPhone = event.phone
                    )
                }
            }

            is ContactEvent.SetCompanyName -> {
                _state.update {
                    it.copy(
                        companyName = event.companyName
                    )
                }
            }

            is ContactEvent.SetContactAddress -> {
                _state.update {
                    it.copy(
                        contactAddress = event.address
                    )
                }
            }

            is ContactEvent.SetContactCity -> {
                _state.update {
                    it.copy(
                        contactCity = event.city
                    )
                }
            }

            is ContactEvent.SetContactCountry -> {
                _state.update {
                    it.copy(
                        contactCountry = event.country
                    )
                }
            }

            is ContactEvent.SetContactFax -> {
                _state.update {
                    it.copy(
                        contactFax = event.fax
                    )
                }
            }

            is ContactEvent.SetContactPostalCode -> {
                _state.update {
                    it.copy(
                        contactPostalCode = event.postalCode
                    )
                }
            }

            is ContactEvent.SetContactTitle -> {
                _state.update {
                    it.copy(
                        contactTitle = event.title
                    )
                }
            }

            is ContactEvent.SetCustomerId -> {
                _state.update {
                    it.copy(
                        customerID = event.customerId
                    )
                }
            }

            ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = true
                    )
                }
            }

            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }

            is ContactEvent.ShowContactDetails -> {
                _state.update {
                    it.copy(
                        selectedContact = event.contact
                    )
                }
            }

            ContactEvent.HideContactDetails -> {
                _state.update {
                    it.copy(
                        selectedContact = null
                    )
                }
            }
        }
    }

    private fun validateContact(): Boolean {
        var isValid = true
        val currentState = _state.value

        _state.update { state ->
            state.copy(
                nameError = if (currentState.contactName.isBlank()) {
                    isValid = false
                    "Name is required"
                } else "",

                emailError = when {
                    currentState.contactEmail.isBlank() -> {
                        isValid = false
                        "Email is required"
                    }

                    !isValidEmail(currentState.contactEmail) -> {
                        isValid = false
                        "Invalid email format"
                    }

                    else -> ""
                }
            )
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private suspend fun isDuplicateContact(contactName: String): Boolean {
        val existingContacts = dao.getContactsByName(contactName)
        return existingContacts.isNotEmpty()
    }
}