package com.example.contactprovider.data

data class Contact(
    val name: String,
    val id: Long,
    val phones: List<String>,
    val emailAddress: List<String>,
)
