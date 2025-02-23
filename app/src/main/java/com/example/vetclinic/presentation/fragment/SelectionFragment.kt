package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentLoginBinding
import com.example.vetclinic.databinding.FragmentSelectionBinding
import com.example.vetclinic.presentation.VetClinicApplication


class SelectionFragment : Fragment() {

    private val args: SelectionFragmentArgs by navArgs()

    private var _binding: FragmentSelectionBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentSelectionBinding is null"
        )

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        val userName = args.userName

        binding.welcomeText.text = String.format("Добро пожаловать,\n%s", userName)


        binding.cardViewDoctors.setOnClickListener {
            launchDoctorsFragment()
        }

        binding.cardViewServices.setOnClickListener {
            launchServicesFragment()
        }
    }

    private fun launchDoctorsFragment() {
        findNavController().navigate(
            SelectionFragmentDirections
                .actionSelectionFragmentToDoctorsFragment()
        )
    }

    private fun launchServicesFragment() {
        TODO()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
