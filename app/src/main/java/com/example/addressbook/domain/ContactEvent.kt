package com.example.addressbook.domain

sealed interface ContactEvent {
    data class SaveContact(val contact: Contact? = null) : ContactEvent
    data class EditContact(val contact: Contact): ContactEvent
    data class ImportSuccess(val count: Int): ContactEvent

    data class SetCustomerId(val customerId: String) : ContactEvent
    data class SetCompanyName(val companyName: String) : ContactEvent
    data class SetContactName(val name: String) : ContactEvent
    data class SetContactTitle(val title: String) : ContactEvent
    data class SetContactAddress(val address: String) : ContactEvent
    data class SetContactCity(val city: String) : ContactEvent
    data class SetContactEmail(val email: String) : ContactEvent
    data class SetContactPostalCode(val postalCode: String) : ContactEvent
    data class SetContactCountry(val country: String) : ContactEvent
    data class SetContactPhone(val phone: String) : ContactEvent
    data class SetContactFax(val fax: String) : ContactEvent

    data object ShowDialog : ContactEvent
    data object HideDialog : ContactEvent

    data class ShowContactDetails(val contact: Contact) : ContactEvent
    object HideContactDetails : ContactEvent

    data class SortContacts(val sortType: SortType) : ContactEvent
    data class DeleteContact(val contact: Contact) : ContactEvent
}
