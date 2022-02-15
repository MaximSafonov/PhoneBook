package com.example.contactprovider.viewmodel.contact_list

import android.app.Application
import androidx.lifecycle.*
import com.example.contactprovider.data.Contact
import com.example.contactprovider.data.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContactListViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ContactRepository = ContactRepository(application)

    private val callMutableFlow = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
    fun callSharedFlow(): SharedFlow<String> {
        return callMutableFlow
    }

    private val contactsMutableFlow = MutableStateFlow<List<Contact>>(emptyList())
    fun contactsStateFlow(): StateFlow<List<Contact>> {
        return contactsMutableFlow
    }

    fun loadList() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val list = repository.getAllContacts()
                    Timber.d("ContactListViewModel loadList list from repo: $list")
                    contactsMutableFlow.value = list
                }
            } catch (t: Throwable) {
                Timber.e("ContactListViewModel loadList contact list error", t)
                contactsMutableFlow.emit(emptyList())
            }
        }
    }

    fun callToContact(contact: Contact) {
        contact.phones.firstOrNull()?.let { callMutableFlow.tryEmit(it) }
    }

}