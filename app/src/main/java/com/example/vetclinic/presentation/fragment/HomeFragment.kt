package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.HomeState
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class HomeFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: HomeViewModel by activityViewModels { viewModelFactory }


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentHomeBinding is null"
        )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.profileButton.setOnClickListener {

            viewModel.userId.observe(viewLifecycleOwner) {
                launchProfileFragment(it)
            }

        }

        binding.cardViewDoctors.setOnClickListener {
            launchDoctorsFragment()
        }

        binding.cardViewServices.setOnClickListener {
            launchServicesFragment()
        }


        //user id
//        viewModel.loadUserName()

        observeViewModel()


    }

    private fun observeViewModel() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            Log.d("HomeFragment", "State changed to: $state")
            when (state) {
                is HomeState.Error -> {
                    Toast.makeText(
                        requireContext(), "An error has occurred: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is HomeState.Loading -> {
                    binding.welcomeText.text = "Добро пожаловать,\nгость"
                }

                is HomeState.Result -> {
                    binding.welcomeText.text =
                        String.format("Добро пожаловать,\n%s", state.userName)
                }

            }
        }
    }


    private fun launchDoctorsFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDoctorsFragment()

        )
    }

    private fun launchServicesFragment() {
        findNavController().navigate(
            HomeFragmentDirections
                .actionHomeFragmentToServicesWithDepFragment()
        )
    }

    private fun launchProfileFragment(userId: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToProfileFragment(userId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}













