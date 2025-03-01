package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.HomeState
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class HomeFragment : Fragment() {

//
//    private val args by navArgs<HomeFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentHomeBinding is null"
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        component.inject(this)

        binding.profileButton.setOnClickListener {
            launchProfileFragment()
        }

        binding.cardViewDoctors.setOnClickListener {
            launchDoctorsFragment()
        }
//
//        binding.cardViewServices.setOnClickListener {
//            launchServicesFragment()
//        }

        observeViewModel()


    }

    private fun observeViewModel() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            Log.d("HomeFragment", "State changed to: $state")
            when (state) {
                is HomeState.Error -> Toast.makeText(
                    requireContext(), "An error has occurred: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                is HomeState.Loading -> binding.welcomeText.text = "Добро пожаловать,\nгость"
                is HomeState.Result ->
                    binding.welcomeText.text =
                        String.format("Добро пожаловать,\n%s", state.userName)
            }
        }
    }


    private fun launchDoctorsFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDoctorsFragment()

        )
    }

//    private fun launchServicesFragment() {
//        findNavController().navigate(
//            HomeFragmentDirections
//                .actionHomeFragmentToServicesWithDepFragment()
//        )


    private fun launchProfileFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToProfileFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


//        val userId = args.userId
//        Log.d("HomeFragment", "Received userId: $userId")
//        viewModel.loadUserName(args.userId)

//        if (savedInstanceState == null) {
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fragment_info_container, InfoFragment())
//                .commit()
//        }
//
//
//        val navController = (childFragmentManager.findFragmentById(R.id.selection_nav_host_fragment)
//                as NavHostFragment).navController
//
//        val clinicInfoContainer = requireView().findViewById<View>(R.id.fragment_info_container)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            clinicInfoContainer.visibility =
//                if (destination.id == R.id.selectionFragment) View.VISIBLE else View.GONE
//        }
//












