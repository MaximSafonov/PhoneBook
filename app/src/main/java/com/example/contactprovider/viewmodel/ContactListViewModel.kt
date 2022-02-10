package com.example.contactprovider.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.contactprovider.data.Contact
import com.example.contactprovider.data.ContactRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ContactRepository = ContactRepository(application)

    private val callMutableFlow = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
    fun callSharedFlow(): SharedFlow<String> {
        return callMutableFlow
    }

    private val contactsMutableFlow = MutableSharedFlow<List<Contact>>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
    fun contactsSharedFlow(): SharedFlow<List<Contact>> {
        return contactsMutableFlow
    }

    fun loadList() {
        viewModelScope.launch {
            try {
                Log.d("ContactListViewModel loadList", "contact list added")
                val list = repository.getAllContacts()
                Log.d("ContactListViewModel loadList", "list from repo: ${list.toString()}")
                contactsMutableFlow.emit(list)
            } catch (t: Throwable) {
                Log.e("ContactListViewModel loadList", "contact list error", t)
                contactsMutableFlow.emit(emptyList())
            }
        }
    }

    fun callToContact(contact: Contact) {
        contact.phones.firstOrNull()?.let {
            callMutableFlow.tryEmit(it)
            Log.d("calltoCantact", "${callMutableFlow.tryEmit(it)} ")
        }
    }
}