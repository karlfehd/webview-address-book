package com.example.addressbook.util

import android.util.Log
import com.example.addressbook.domain.Contact
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object XmlParser {

    fun parseXml(xmlContent: String): List<Contact> {
        if (xmlContent.isBlank()) {
            throw IllegalArgumentException("Empty file")
        }

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xmlContent))

        val contacts = mutableListOf<Contact>()
        var eventType = parser.eventType
        var currentContact: Contact? = null
        var currentTag = ""
        var hasValidStructure = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    //Log.d("XmlParser", "Current tag: $currentTag")
                    if (currentTag == "AddressBook") hasValidStructure = true
                    if (currentTag == "Contact") {
                        currentContact = Contact(
                            customerID = "",
                            companyName = "",
                            contactName = "",
                            contactTitle = "",
                            address = "",
                            city = "",
                            country = "",
                            postalCode = "",
                            phone = "",
                            email = "",
                            fax = ""
                        )
                    }
                }

                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isNotEmpty() && currentContact != null) {
                        Log.d("XmlParser", "Processing tag: $currentTag with text: $text")
                        when (currentTag) {
                            "CustomerID" -> currentContact?.customerID = text
                            "CompanyName" -> currentContact?.companyName = text
                            "ContactName" -> currentContact?.contactName = text
                            "ContactTitle" -> currentContact?.contactTitle = text
                            "Address" -> currentContact?.address = text
                            "City" -> currentContact?.city = text
                            "Country" -> currentContact?.country = text
                            "PostalCode" -> currentContact?.postalCode = text
                            "Phone" -> currentContact?.phone = text
                            "Email" -> currentContact?.email = text
                            "Fax" -> currentContact?.fax = text
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    //Log.d("XmlParser", "End tag: ${parser.name}")
                    if (parser.name == "Contact") {
                        currentContact?.let {
                            Log.d(
                                "XmlParser",
                                "Completed Contact: name=${it.contactName}, email=${it.email}, phone=${it.phone}"
                            )
                            if (it.isValid()) {
                                contacts.add(it)
                                Log.d(
                                    "XmlParser",
                                    "Added valid contact. Total contacts: ${contacts.size}"
                                )
                            }
                        }
                        currentContact = null
                        currentTag = ""
                    }
                }
            }
            eventType = parser.next()
        }
        Log.d("XmlParser", "Contact count: ${contacts.size}")

        if (!hasValidStructure) {
            throw IllegalArgumentException("Invalid XML format")
        }

        if (contacts.isEmpty()) {
            throw IllegalArgumentException("No valid contacts found")
        }

        return contacts
    }

    fun parseContactsXml(xmlContent: String): ImportResult {
        return try {
            val contacts = parseXml(xmlContent)
            ImportResult.Success(contacts.size, contacts)
        } catch (e: IllegalArgumentException) {
            when (e.message) {
                "Empty file" -> ImportResult.EmptyFile
                "Invalid XML format" -> ImportResult.InvalidFormat
                "No valid contacts found" -> ImportResult.EmptyFile
                else -> ImportResult.Error(e.message ?: "Unknown error occurred")
            }
        } catch (e: Exception) {
            ImportResult.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    private fun Contact.isValid(): Boolean {
        val isValidContact = contactName.isNotBlank() && email.isNotBlank()
        Log.d(
            "XmlParser",
            "Contact validation - name: $contactName, email: $email, phone: $phone, isValid: $isValidContact"
        )
        return isValidContact
    }
}