package com.example.addressbook.presentation

import com.example.addressbook.domain.Contact
import com.example.addressbook.domain.SortType

data class ContactState(
    val contacts: List<Contact> = emptyList(),

    val customerID: String = "",
    val companyName: String = "",
    val contactName: String = "",
    val contactTitle: String = "",
    val contactAddress: String = "",
    val contactCity: String = "",
    val contactEmail: String = "",
    val contactPostalCode: String = "",
    val contactCountry: String = "",
    val contactPhone: String = "",
    val contactFax: String = "",

    val isAddingContact: Boolean = false,
    val sortType: SortType = SortType.NAME
)
