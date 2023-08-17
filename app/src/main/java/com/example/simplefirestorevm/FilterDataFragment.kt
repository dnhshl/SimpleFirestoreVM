package com.example.simplefirestorevm

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.simplefirestorevm.databinding.FragmentEnterDataBinding
import com.example.simplefirestorevm.databinding.FragmentFilterDataBinding
import com.example.simplefirestorevm.databinding.FragmentLoginstatusBinding
import com.example.simplefirestorevm.model.FirestoreViewModel

class FilterDataFragment : Fragment() {
    private var _binding: FragmentFilterDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm: FirestoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}