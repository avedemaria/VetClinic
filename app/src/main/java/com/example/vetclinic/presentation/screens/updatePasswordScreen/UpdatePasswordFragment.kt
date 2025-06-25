package com.example.vetclinic.presentation.screens.updatePasswordScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vetclinic.MainActivity
import com.example.vetclinic.R
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentUpdatePasswordBinding
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber


class UpdatePasswordFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUpdatePasswordBinding is null"
        )

    private val mode: PasswordUpdateMode by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(ARG_MODE, PasswordUpdateMode::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(ARG_MODE) as? PasswordUpdateMode
        } ?: throw IllegalStateException("PasswordUpdateMode is missing")
    }

    private val viewModel: UpdatePasswordViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var resetPasswordUseCase: ResetPasswordUseCase


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        binding.updatePasswordButton.setOnClickListener {
            val newPassword = binding.etEnterNewPassword.text.toString()
            val confirmPassword = binding.etConfirmNewPassword.text.toString()
            viewModel.updatePassword(newPassword, confirmPassword)
        }
        observeViewModel()
    }


    private fun observeViewModel() {
        handleState()
        handleEvent()
    }


    private fun handleState() {
        viewModel.updatePasswordState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is UpdatePasswordState.Error -> Timber.tag(TAG).d("Заглушка для Error")
                is UpdatePasswordState.Loading -> Timber.tag(TAG).d("Заглушка для Loading")
                is UpdatePasswordState.Success -> {

                    when (mode) {
                        PasswordUpdateMode.FROM_DEEPLINK -> launchLoginFragment()
                        PasswordUpdateMode.FROM_ACCOUNT -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.password_updated),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            parentFragmentManager.popBackStack()
                        }
                    }
                }

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

    private fun launchLoginFragment() {

        Toast.makeText(
            requireContext(), getString(R.string.password_updated),
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "UpdatePasswordFragment"
        private const val ARG_MODE = "arg_mode"

        fun newInstance(mode: PasswordUpdateMode): UpdatePasswordFragment {
            val fragment = UpdatePasswordFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(ARG_MODE, mode)
            }
            return fragment
        }
    }
}