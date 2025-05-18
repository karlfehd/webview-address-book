package com.example.addressbook.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    var customerID: String = "",
    var companyName: String = "",
    var contactName: String, // required
    var contactTitle: String = "",
    var address: String = "",
    var city: String = "",
    var email: String,  // required
    var postalCode: String = "",
    var country: String = "",
    var phone: String = "",
    var fax: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)