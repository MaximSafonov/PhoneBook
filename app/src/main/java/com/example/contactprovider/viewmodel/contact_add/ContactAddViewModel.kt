package com.example.contactprovider.viewmodel.contact_add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactprovider.data.ContactAddRepository
import kotlinx.coroutines.launch

class ContactAddViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ContactAddRepository = ContactAddRepository(application)

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            repository.saveContact(name, phone)
        }
    }
}