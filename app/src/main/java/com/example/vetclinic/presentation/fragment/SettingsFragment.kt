package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentSettingsBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.SettingsState
import com.example.vetclinic.presentation.viewmodel.SettingsViewModel
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButtonToggleGroup
import jakarta.inject.Inject

class SettingsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

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


        binding.btnLogOut.setOnClickListener {
            viewModel.logOut()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()  // Возврат к предыдущему фрагменту
                }
            })

        observeViewModel()

    }


    private fun observeViewModel() {
        viewModel.settingsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsState.Error -> Toast.makeText(
                    requireContext(),
                    "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                SettingsState.Loading -> Log.d(
                    "SettingsFragment",
                    "Loading - заглушка для теста"
                )

                SettingsState.LoggedOut -> launchLoginFragment()
            }
        }
    }


    private fun launchLoginFragment() {
        // TODO: переделать на NavHostFragment.findNavController(this).navigate(R.id.action_settingsFragment_to_loginFragment)
        Log.d("SettingsFragment", "logged out")

        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(intent)
        requireActivity().finish()

    }


    private fun hideToggleGroup() {
        (parentFragment as? ProfileFragment)?.updateToggleGroupVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}