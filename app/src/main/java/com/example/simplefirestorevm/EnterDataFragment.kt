package com.example.simplefirestorevm

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.simplefirestorevm.databinding.FragmentEnterDataBinding
import com.example.simplefirestorevm.databinding.FragmentLoginstatusBinding
import com.example.simplefirestorevm.model.FirestoreViewModel
import com.example.simplefirestorevm.model.LoginState
import com.example.simplefirestorevm.model.LoginViewModel


class EnterDataFragment : Fragment() {
    private var _binding: FragmentEnterDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dbvm: FirestoreViewModel by activityViewModels()
    private val authvm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authvm.loginState.observe(viewLifecycleOwner) { loginState ->
            when (loginState) {
                LoginState.LoggedIn -> {
                    binding.tvLoginStatus.text = getString(R.string.loggedinOK)
                    binding.tvLoginStatus.setTextColor(Color.GREEN)
                }
                else -> {
                    binding.tvLoginStatus.text = getString(R.string.loggedinNOK)
                    binding.tvLoginStatus.setTextColor(Color.RED)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}