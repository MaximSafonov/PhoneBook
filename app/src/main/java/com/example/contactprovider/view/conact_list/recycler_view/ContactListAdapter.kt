package com.example.contactprovider.view.conact_list.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.example.contactprovider.data.Contact
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ContactListAdapter(
    onContactToCallClick: (Contact) -> Unit,
    onContactClick: (Contact) -> Unit
    ): AsyncListDifferDelegationAdapter<Contact>(
    ContactCallback()
) {

    init {
        delegatesManager.addDelegate(ContactAdapterDelegate(onContactToCallClick, onContactClick))
    }

    class ContactCallback: DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.name == oldItem.name
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == oldItem
        }

    }
}