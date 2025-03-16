package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentPetBinding
import com.example.vetclinic.databinding.FragmentResetPasswordWithEmailBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.ResetPasswordState
import com.example.vetclinic.presentation.viewmodel.ResetPasswordViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class ResetPasswordWithEmailFragment : Fragment() {


    private val args by navArgs<ResetPasswordWithEmailFragmentArgs>()

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    private var _binding: FragmentResetPasswordWithEmailBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentResetPasswordWithEmailBinding? is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }



    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ResetPasswordViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordWithEmailBinding.inflate(
            inflater, container,
            false
        )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailEditText.setText(args.email)

        binding.sendEmailButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            saveEmailToPreferences(email)
            viewModel.sendResetLink(email)
        }
        observeViewModel()


    }

    private fun saveEmailToPreferences(email: String) {
        sharedPreferences.edit().apply {
            putString("user_email", email)
            apply()
        }
    }


    private fun observeViewModel() {
        viewModel.resetPasswordState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResetPasswordState.Error -> Toast.makeText(
                    requireContext(),
                    "Возникла ошибка ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                ResetPasswordState.Loading -> Log.d(TAG, "Заглушка для Loading")
                ResetPasswordState.Success -> Toast.makeText(
                    requireContext(),
                    "Ссылка для восстановления пароля была отправлена на ваш email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ResetPasswordWithEmailFragment"
    }

}