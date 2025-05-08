package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vetclinic.databinding.FragmentSettingsBinding
import com.example.vetclinic.MainActivity
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.ProfileFragment
import com.example.vetclinic.presentation.providers.ViewModelFactory
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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        hideToggleGroup()

        binding.llDeleteAccount.setOnClickListener {
           showDeleteConfirmationDialog()
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


    private fun showDeleteConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Удаление аккаунта")
            .setMessage(
                "Вы уверены, что хотите удалить этот аккаунт?" +
                        " Все данные будут удалены "
            )
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteAccount()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()

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