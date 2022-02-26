package com.example.contactprovider.viewmodel.contact_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactprovider.data.Contact
import com.example.contactprovider.data.ContactListRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactListRepository = ContactListRepository(application)

    private val callMutableFlow = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    fun callSharedFlow(): SharedFlow<String> {
        return callMutableFlow
    }

    private val contactsMutableFlow = MutableStateFlow(emptyList<Contact>())
    val contactStateFlow = contactsMutableFlow.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            try {
                val list = repository.getAllContacts()
                contactsMutableFlow.value = list
            } catch (t: Throwable) {
                contactsMutableFlow.emit(emptyList())
            }
        }
    }

    fun callToContact(contact: Contact) {
        contact.phones.firstOrNull()?.let { callMutableFlow.tryEmit(it) }
    }

}