package com.example.contactprovider.data

import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.regex.Pattern

class ContactAddRepository(private val context: Context) {

    private val phonePattern = Pattern.compile("^\\+?[0-9]{3}-?[0-9]{6,12}\$")

    suspend fun saveContact(name: String, phone: String) = withContext(Dispatchers.IO) {
        if (phonePattern.matcher(phone).matches().not() || name.isBlank()) {
            throw IncorrectFormException()
        }
        val contactId = saveRawContact()
        saveContactName(contactId, name)
        saveContactPhone(contactId, phone)
    }

    private fun saveRawContact(): Long {
        val uri = context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, ContentValues())
        Timber.d("saveRawContact uri = $uri")
        return uri?.lastPathSegment?.toLongOrNull() ?: error("cannot save raw contact")
    }

    private fun saveContactName(contactId: Long, name: String) {
        val contentValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        }
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
    }

    private fun saveContactPhone(contactId: Long, phone: String) {
        val contentValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
        }
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
    }
}