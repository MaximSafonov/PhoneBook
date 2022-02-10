package com.example.contactprovider.view.conact_list.recycler_view

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactprovider.R
import com.example.contactprovider.data.Contact
import com.example.contactprovider.utils.inflate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer

class ContactAdapterDelegate(
    private val onContactToCallClick: (Contact) -> Unit,
    private val onContactClick: (Contact) -> Unit): AbsListItemAdapterDelegate<Contact, Contact, ContactAdapterDelegate.Holder>() {

    override fun isForViewType(item: Contact, items: MutableList<Contact>, position: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(parent.inflate(R.layout.item_contact), onContactToCallClick, onContactClick)
    }

    override fun onBindViewHolder(item: Contact, holder: Holder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    class Holder(
        override val containerView: View,
        onContactToCallClick: (Contact) -> Unit,
        onContactClick: (Contact) -> Unit
    ): RecyclerView.ViewHolder(containerView), LayoutContainer{

        private var currentContact: Contact? = null
        private var contactNameTextView = containerView.findViewById<TextView>(R.id.contactNameTextView)
        private var contactPhoneTextView = containerView.findViewById<TextView>(R.id.contactPhone)
        private var callButton = containerView.findViewById<FloatingActionButton>(R.id.callButton)

        init {
            callButton.setOnClickListener { currentContact?.let(onContactToCallClick) }
            containerView.setOnClickListener { currentContact?.let(onContactClick) }
        }

        fun bind(contact: Contact) {
            currentContact = contact
            contactNameTextView.text = contact.name
            contactPhoneTextView.text = contact.phones.joinToString("\n")
        }
    }
}