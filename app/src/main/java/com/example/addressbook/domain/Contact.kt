package com.example.addressbook.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val customerID: String = "",
    val companyName: String = "",
    val contactName: String, // required
    val contactTitle: String = "",
    val address: String = "",
    val city: String = "",
    val email: String,  // required
    val postalCode: String = "",
    val country: String = "",
    val phone: String = "",
    val fax: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)