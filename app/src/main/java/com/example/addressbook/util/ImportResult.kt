package com.example.addressbook.util

import com.example.addressbook.domain.Contact

sealed class ImportResult {
    data class Success(val count: Int, val contacts: List<Contact>) : ImportResult()
    data class Error(val message: String) : ImportResult()
    data object InvalidFormat : ImportResult()
    data object EmptyFile : ImportResult()
}