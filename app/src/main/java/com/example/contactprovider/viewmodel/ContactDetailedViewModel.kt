package com.example.contactprovider.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactprovider.data.ContactRepository
import kotlinx.coroutines.launch

class ContactDetailedViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ContactRepository = ContactRepository(application)

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }

}