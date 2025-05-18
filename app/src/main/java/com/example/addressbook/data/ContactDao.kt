package com.example.addressbook.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.addressbook.domain.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    suspend fun insertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY contactName ASC")
    fun getContactsSortedByName(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY email ASC")
    fun getContactsSortedByEmail(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE contactName = :contactName")
    suspend fun getContactsByName(contactName: String): List<Contact>

}
