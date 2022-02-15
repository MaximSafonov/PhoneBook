package com.example.contactprovider.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContactRepository(
    private val context: Context
) {

    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        context.contentResolver.query(
         ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use {
            getContactsFromCursor(it)
        }.orEmpty()
    }

    private suspend fun getContactsFromCursor(cursor: Cursor): List<Contact> = withContext(Dispatchers.IO){
        if(cursor.moveToFirst().not()) return@withContext emptyList()
        val list = mutableListOf<Contact>()
        do {
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val name = cursor.getString(nameIndex).orEmpty()

            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val id = cursor.getLong(idIndex)

            list.add(Contact(id = id, name = name, phones = getPhonesForContact(id), emailAddress = getEmailForContact(id)))
        } while (cursor.moveToNext())

        return@withContext list
    }

    private suspend fun getPhonesForContact(contactId: Long): List<String> = withContext(Dispatchers.IO) {
        return@withContext context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use {
            getPhonesFromCursor(it)
        }.orEmpty()
    }

    private suspend fun getPhonesFromCursor(cursor: Cursor): List<String> = withContext(Dispatchers.IO) {
        if(cursor.moveToFirst().not()) return@withContext emptyList()
        val list = mutableListOf<String>()
        do {
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val number = cursor.getString(numberIndex)
            list.add(number)
        } while (cursor.moveToNext())

        return@withContext list
    }

        private suspend fun getEmailForContact(contactId: Long): List<String> =
        withContext(Dispatchers.IO) {
        Timber.d("getEmailForContact launched")
            return@withContext context.contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                arrayOf(contactId.toString()),
                null
            )?.use {
                getEmailsFromCursor(it)
            }.orEmpty()
    }

    private suspend fun getEmailsFromCursor(cursor: Cursor): List<String> =
        withContext(Dispatchers.IO) {
        if(cursor.moveToFirst().not()) return@withContext emptyList()
        val list = mutableListOf<String>()
        do {
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            val emailAddress = cursor.getString(numberIndex).orEmpty()
            list.add(emailAddress)
        } while (cursor.moveToNext())
        Timber.d("getEmailsFromCursor launched")
            return@withContext list
    }


    suspend fun deleteContact(contactId: Long) {
        withContext(Dispatchers.IO) {
            val contactUri =
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
            context.contentResolver.delete(contactUri, null, null)
        }
    }
}