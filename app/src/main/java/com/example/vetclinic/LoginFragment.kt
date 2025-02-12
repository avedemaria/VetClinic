package com.example.vetclinic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentVideoRecorderBinding is null"
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnLogin.setOnClickListener {
            launchMainFragment()
        }

    }

    private fun launchMainFragment() {
        findNavController().navigate(
            LoginFragmentDirections
                .actionLoginFragmentToMainFragment()
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}