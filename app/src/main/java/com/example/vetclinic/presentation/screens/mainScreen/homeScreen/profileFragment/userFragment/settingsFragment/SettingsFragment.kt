package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vetclinic.MainActivity
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentSettingsBinding
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.ProfileFragment
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        hideToggleGroup()

        binding.llDeleteAccount.setOnClickListener {
//           showDeleteConfirmationDialog()
            Snackbar.make(binding.root, "Раздел находится в разработке", Snackbar.LENGTH_SHORT)
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
                    parentFragmentManager.popBackStack()
                }
            })

        observeViewModel()

    }


    private fun observeViewModel() {
        handleState()
        handleEvent()
    }


    private fun handleState() {
        viewModel.settingsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsState.Error -> Timber.tag(TAG).d("Error - заглушка для теста")

                is SettingsState.Loading -> Timber.tag(TAG).d("Loading - заглушка для теста")

                is SettingsState.LoggedOut -> launchLoginFragment()
            }
        }
    }

    private fun handleEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> Snackbar.make(
                            binding.root,
                            event.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
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

    companion object {
        private const val TAG = "SettingsFragment"
    }
}