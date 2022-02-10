package com.example.contactprovider.data

import java.util.*
data class Contact(

    val name: String,
    val id: Long,
    val phones: List<String>,
    val emailAddress: List<String>,
)
