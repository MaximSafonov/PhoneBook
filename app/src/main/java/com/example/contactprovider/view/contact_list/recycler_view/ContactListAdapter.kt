package com.example.contactprovider.view.contact_list.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.example.contactprovider.data.Contact
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ContactListAdapter(
    onContactToCallClick: (Contact) -> Unit,
    onContactClick: (Contact) -> Unit
): AsyncListDifferDelegationAdapter<Contact>(ContactCallBack()) {

    init {
        delegatesManager.addDelegate(ContactListAdapterDelegate(onContactToCallClick, onContactClick))
    }

    class ContactCallBack: DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return newItem.name == oldItem.name
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return newItem == oldItem
        }
    }
}