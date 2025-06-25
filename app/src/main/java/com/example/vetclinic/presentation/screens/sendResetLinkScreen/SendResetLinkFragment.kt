package com.example.vetclinic.presentation.screens.sendResetLinkScreen

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.databinding.FragmentResetPasswordWithEmailBinding
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class SendResetLinkFragment : Fragment() {


    private val args by navArgs<SendResetLinkFragmentArgs>()


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

    private val viewModel: SendResetLinkViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
            viewModel.sendResetLink(email)
        }
        observeViewModel()


    }



    private fun observeViewModel() {
        handleState()
        handleEvent()
    }


    private fun handleState () {
        viewModel.sendResetLinkState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SendResetLinkState.Error -> Log.d(TAG, "Заглушка для Loading")
                SendResetLinkState.Loading -> Log.d(TAG, "Заглушка для Loading")
                SendResetLinkState.Success ->  findNavController().popBackStack()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ResetPasswordWithEmailFragment"
    }

}