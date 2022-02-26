package com.example.contactprovider.viewmodel.contact_detailed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactprovider.data.ContactListRepository
import kotlinx.coroutines.launch

class ContactDetailedViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ContactListRepository = ContactListRepository(application)

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }

}