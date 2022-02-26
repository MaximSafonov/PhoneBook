package com.example.contactprovider.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B: ViewBinding>: Fragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initBinding(inflater, container)
        return binding.root
    }

    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
/*
 *Пишешь вместо этой дичи binding: ItemContactBinding
 * (если у тебя разметка айтема названа как item_contact.xml) и
 * для view подаешь binding.root. Дальше избавишься от методов findViewById.
 */