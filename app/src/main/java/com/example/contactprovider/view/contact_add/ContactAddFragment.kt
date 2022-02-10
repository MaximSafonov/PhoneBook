package com.example.contactprovider.view.contact_add

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.contactprovider.R
import com.example.contactprovider.databinding.FragmentAddBinding
import com.example.contactprovider.viewmodel.ContactAddViewModel

class ContactAddFragment: Fragment(R.layout.fragment_add) {

    private val contactAddViewModel: ContactAddViewModel by viewModels()

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_CONTACTS
                ) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    contactAddViewModel.addContact(
                        binding.nameTextField.editText?.text?.toString().orEmpty(),
                        binding.phoneTextField.editText?.text?.toString().orEmpty()
                    )
                    findNavController().apply {
                        popBackStack()
                    }
                }
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS) -> {
                    showWriteContactsRationaleDialog()
                } else -> {
                requestWriteContactsPermission()
                }
            }
        }
    }

    private fun showWriteContactsRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Необходимо разрешение для записи нового контакта")
            .setPositiveButton("OK") { _, _ -> requestWriteContactsPermission() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun requestWriteContactsPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_CONTACTS),
            ContactAddFragment.PERMISSION_REQUEST_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 4321
    }
}