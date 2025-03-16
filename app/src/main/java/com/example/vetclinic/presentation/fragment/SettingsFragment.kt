package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentSettingsBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.google.android.material.button.MaterialButtonToggleGroup

class SettingsFragment : Fragment() {


    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentSettingsBinding is null"
        )

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        hideToggleGroup()

        binding.llDeleteAccount.setOnClickListener {
            Toast.makeText(
                requireContext(), "раздел находится в разработке",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()  // Возврат к предыдущему фрагменту
                }
            })

    }


    private fun hideToggleGroup() {
        (parentFragment as? ProfileFragment)?.updateToggleGroupVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}