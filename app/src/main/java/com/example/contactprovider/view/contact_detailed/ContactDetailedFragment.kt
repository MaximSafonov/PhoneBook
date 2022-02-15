package com.example.contactprovider.view.contact_detailed

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.contactprovider.databinding.FragmentDetailedBinding
import com.example.contactprovider.view.BaseFragment
import com.example.contactprovider.viewmodel.contact_detailed.ContactDetailedViewModel
import timber.log.Timber

class ContactDetailedFragment: BaseFragment<FragmentDetailedBinding>() {

    private val contactDetailedViewModel: ContactDetailedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailedName.text = arguments?.getString("KEY_NAME").orEmpty()
        binding.detailedPhone.text = arguments?.getString("KEY_PHONE").orEmpty()
        binding.detailedEmail.text = arguments?.getString("KEY_EMAIL").orEmpty()

        binding.buttonDelete.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_CONTACTS
                ) ==
                        PackageManager.PERMISSION_GRANTED -> {
                            contactDetailedViewModel.deleteContact(
                                arguments?.getLong("KEY_ID")?: 0
                            )
                    }
                        shouldShowRequestPermissionRationale(
                            Manifest.permission.WRITE_CONTACTS) -> {
                            showWriteContactsRationaleDialog()
                } else -> {
                            requestWriteContactsPermission()
                    }
            }
        }
        Timber.d("ContactListDetailedFragment launched ${arguments?.getString("KEY_NAME")}")
    }

    private fun showWriteContactsRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Необходимо разрешение для удаления контакта")
            .setPositiveButton("OK") { _, _ -> requestWriteContactsPermission() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun requestWriteContactsPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 4321
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailedBinding {
        return FragmentDetailedBinding.inflate(inflater, container, false)
    }
}