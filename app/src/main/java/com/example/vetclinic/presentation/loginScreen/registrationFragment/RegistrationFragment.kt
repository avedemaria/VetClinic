package com.example.vetclinic.presentation.loginScreen.registrationFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.vetclinic.databinding.FragmentRegistrationBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.RegistrationAdapter
import com.example.vetclinic.presentation.PetInput
import com.example.vetclinic.presentation.ViewModelFactory
import jakarta.inject.Inject


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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.isUserInputEnabled = false

        registrationPagerAdapter = RegistrationAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = registrationPagerAdapter


        setUpSmoothPaging()

        binding.btnCreateAccount.setOnClickListener {
            val userFragment = childFragmentManager.findFragmentByTag("f0") as? UserInput
            val petFragment = childFragmentManager.findFragmentByTag("f1") as? PetInput

            val userData = userFragment?.collectUserInput()
            val petData = petFragment?.collectPetInput()

            if (userData != null && petData != null) {
                viewModel.updateFormState(userData, petData)
            }

            viewModel.registerUser()
        }



        binding.tvBackToLogin.setOnClickListener {
            launchLoginFragment()
        }

        observeViewModel()


    }

    private fun observeViewModel() {

        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "Received registration state: $state")
            when (state) {
                is RegistrationState.Error -> Toast.makeText(
                    requireContext(),
                    "An error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                is RegistrationState.Result -> {
                    Log.d(TAG, "Form data updated: user=${state.userdata}, pet=${state.petData}")
                }

                RegistrationState.Loading -> Log.d(TAG, "Заглушка loading")
                RegistrationState.Success -> launchMainFragment()
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


//    private fun generateMockForm() {
//        binding.etName.setText("John")
//        binding.etLastName.setText("Doe")
//        binding.etPetName.setText("Rex")
//        binding.etPhoneNumber.setText("123456789")
//        binding.etEmail.setText("test${UUID.randomUUID()}@test.com")
//        binding.etPassword.setText("password")
//    }


    companion object {
        private const val TAG = "RegistrationFragment"
    }

}







