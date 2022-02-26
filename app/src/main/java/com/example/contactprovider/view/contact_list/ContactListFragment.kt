package com.example.contactprovider.view.contact_list

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.contactprovider.R
import com.example.contactprovider.data.Contact
import com.example.contactprovider.databinding.FragmentListBinding
import com.example.contactprovider.utils.autoCleared
import com.example.contactprovider.utils.launchWhenStarted
import com.example.contactprovider.view.BaseFragment
import com.example.contactprovider.view.contact_list.recycler_view.ContactListAdapter
import com.example.contactprovider.viewmodel.contact_list.ContactListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ContactListFragment : BaseFragment<FragmentListBinding>() {

    private val viewModel by viewModels<ContactListViewModel>()
    private var contactAdapter by autoCleared<ContactListAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        bindViewModel()
        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_contactAddFragment)
        }
        onLoadList()
    }

    private fun initList() {
        contactAdapter =
            ContactListAdapter(viewModel::callToContact) { contact -> navigateToDetails(contact) }

        with(binding.contactList) {
            adapter = contactAdapter
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
        viewModel.contactStateFlow.onEach { listContacts ->
            contactAdapter.items = listContacts
        }.launchWhenStarted(viewLifecycleOwner.lifecycleScope)


        viewModel.callSharedFlow().onEach { phone ->
            callToPhone(phone)
        }.launchWhenStarted(lifecycleScope)
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
            "KEY_EMAIL" to contact.emailAddress.joinToString("\n")
        )
        findNavController().navigate(
            R.id.action_listFragment_to_contactDetailedFragment,
            bundle
        )
    }

    private fun onLoadList() {
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

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentListBinding {
        return FragmentListBinding.inflate(inflater, container, false)
    }
}