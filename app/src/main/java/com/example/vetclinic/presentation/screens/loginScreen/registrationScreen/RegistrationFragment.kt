package com.example.vetclinic.presentation.screens.loginScreen.registrationScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentRegistrationBinding
import com.example.vetclinic.presentation.adapter.RegistrationAdapter
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.presentation.widgets.PetInput
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber


class RegistrationFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RegistrationViewModel by viewModels {
        viewModelFactory
    }


    private lateinit var registrationPagerAdapter: RegistrationAdapter

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentRegistrationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentRegistrationBinding is null"
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        binding.viewPager.isUserInputEnabled = false

        registrationPagerAdapter = RegistrationAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = registrationPagerAdapter

        setUpSmoothPaging()

        binding.btnCreateAccount.setOnClickListener {
            val userFragment = childFragmentManager.findFragmentByTag("f0") as? UserInput
            val petFragment = childFragmentManager.findFragmentByTag("f1") as? PetInput

            val userData = userFragment?.collectUserInput()
            val petData = petFragment?.collectPetInput()


            viewModel.updateFormState(userData, petData)
            viewModel.registerUser()
        }

        binding.tvBackToLogin.setOnClickListener {
            launchLoginFragment()
        }

        observeViewModel()


    }

    private fun observeViewModel() {
        handleState()
        handleEvent()
    }


    private fun handleState() {
        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistrationState.Error -> Timber.tag(TAG).d("Заглушка error")

                is RegistrationState.Result -> {
                    Timber.tag(TAG)
                        .d("Form data updated: user=${state.userdata}, pet=${state.petData}")
                }

                is RegistrationState.Loading -> Timber.tag(TAG).d("Заглушка loading")
                is RegistrationState.Success -> launchMainFragment()
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


    private fun setUpSmoothPaging() {
        val totalPages = 2
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position)
            }
        })

        binding.llNextForm.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition < totalPages - 1) {
                binding.viewPager.setCurrentItem(currentPosition + 1, true)
            }
        }

        binding.llPreviousForm.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition > 0) {
                binding.viewPager.setCurrentItem(currentPosition - 1, true)
            }
        }


        updateButtons(binding.viewPager.currentItem)
    }


    private fun updateButtons(position: Int) {
        val totalPages = 2
        binding.llNextForm.isVisible =
            position < totalPages - 1
        binding.llPreviousForm.isVisible =
            position > 0
    }


    private fun launchMainFragment() {
        findNavController().navigate(
            RegistrationFragmentDirections
                .actionRegistrationFragmentToMainFragment()
        )
    }

    private fun launchLoginFragment() {
        findNavController().navigate(
            RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "RegistrationFragment"
    }

}







