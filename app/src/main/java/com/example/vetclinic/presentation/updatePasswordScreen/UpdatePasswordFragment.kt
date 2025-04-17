package com.example.vetclinic.presentation.updatePasswordScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentUpdatePasswordBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.sendResetLinkScreen.SendResetLinkState
import com.example.vetclinic.presentation.sendResetLinkScreen.SendResetLinkViewModel
import com.example.vetclinic.presentation.ViewModelFactory
import jakarta.inject.Inject


class UpdatePasswordFragment : Fragment() {



    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: UpdatePasswordViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUpdatePasswordBinding is null"
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
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updatePasswordButton.setOnClickListener {

            val token = arguments?.getString(TOKEN)
                ?: throw IllegalArgumentException("No token has been found")

            Log.d("UpdatePasswordFragment", "onCreate received token: $token")
            val newPassword = binding.etConfirmNewPassword.text.toString()

                viewModel.updatePassword(newPassword, token)

        }

        observeViewModel()
    }



    private fun observeViewModel() {
        viewModel.updatePasswordState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is UpdatePasswordState.Error -> Toast.makeText(
                    requireContext(),
                    "Возникла ошибка ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                UpdatePasswordState.Loading -> Log.d(TAG, "Заглушка для Loading")
                UpdatePasswordState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Пароль успешно обновлен",
                        Toast.LENGTH_SHORT
                    ).show()

                    launchLoginFragment()
                }

            }
        }
    }

    private fun launchLoginFragment() {
        findNavController().navigate(
            UpdatePasswordFragmentDirections
                .actionUpdatePasswordFragmentToLoginFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "UpdatePasswordFragment"
        private const val TOKEN = "token"
    }
}