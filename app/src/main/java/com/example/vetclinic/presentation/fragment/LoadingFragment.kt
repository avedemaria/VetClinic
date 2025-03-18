package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.databinding.FragmentLoadingBinding
import com.example.vetclinic.presentation.VetClinicApplication


class LoadingFragment : Fragment() {


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentLoadingBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentLoadingBinding is null"
        )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.all
//        val userId = editor["userId"].toString()

        val userId = sharedPreferences.getString("userId", null)

        if (!userId.isNullOrEmpty()) {
            findNavController().navigate(
                LoadingFragmentDirections.actionLoadingFragmentToMainFragment(
                    userId
                )
            )
        } else {
            findNavController().navigate(LoadingFragmentDirections.actionLoadingFragmentToLoginFragment())
        }
    }
}