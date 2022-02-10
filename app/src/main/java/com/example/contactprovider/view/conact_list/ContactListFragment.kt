package com.example.contactprovider.view.conact_list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactprovider.R
import com.example.contactprovider.data.Contact
import com.example.contactprovider.databinding.FragmentListBinding
import com.example.contactprovider.utils.autoCleared
import com.example.contactprovider.view.conact_list.recycler_view.ContactListAdapter
import com.example.contactprovider.viewmodel.ContactListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactListFragment: Fragment(R.layout.fragment_list) {

    private val viewModel by viewModels<ContactListViewModel>()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var contactAdapter by autoCleared<ContactListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        initList()
        bindViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {
            findNavController().navigate(ContactListFragmentDirections.actionListFragmentToContactAddFragment())
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) ==
                            PackageManager.PERMISSION_GRANTED -> {
                        viewModel.loadList()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                        showWriteContactsRationaleDialog()
                    }
                    else -> {
                        requestWriteContactsPermission()
                    }
                }
            }
        }
        Log.d("ListFragment", "${contactAdapter.items}")
    }

    private fun initList() {
        Log.d("initlist", "lauched")
        contactAdapter = ContactListAdapter(viewModel::callToContact) { contact -> navigateToDetails(contact) }
        with(binding.contactList) {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun bindViewModel() {
        val contactListFlow = viewModel.contactsSharedFlow()
        lifecycleScope.launch {
            contactListFlow.collect { listContacts ->
                contactAdapter.items = listContacts
                Log.d("ListFragment bindviewmodel",
                    "Launch when resumed ${contactAdapter.items}")
            }
        }

        val callFlow = viewModel.callSharedFlow()
        lifecycleScope.launch {
            callFlow.collect { phone ->
                callToPhone(phone)
                Log.d("ListFragment bindviewmodel", "Launch when resumed PHONE: $phone")
            }
        }
    }

    private fun callToPhone(phone: String) {
        Intent(Intent.ACTION_DIAL)
            .apply { data = Uri.parse("tel:$phone") }
            .also { startActivity(it) }
    }

    private fun navigateToDetails(contact: Contact) {
        val bundle = bundleOf(
            "KEY_NAME" to contact.name,
            "KEY_PHONE" to contact.phones.joinToString("\n"),
            "KEY_ID" to contact.id,
            "KEY_EMAIL" to contact.emailAddress.joinToString("\n"))
        findNavController().navigate(
            R.id.action_listFragment_to_contactDetailedFragment,
            bundle
        )
    }

    private fun showWriteContactsRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Необходимо разрешение для загрузки данных контактной книги")
            .setPositiveButton("OK") { _, _ -> requestWriteContactsPermission() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun requestWriteContactsPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 4321
    }
}