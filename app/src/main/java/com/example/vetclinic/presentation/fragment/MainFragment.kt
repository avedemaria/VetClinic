package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.MainSharedViewModel
import com.example.vetclinic.presentation.viewmodel.SelectionState
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class MainFragment : Fragment() {


    private val args by navArgs<MainFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedViewModel: MainSharedViewModel by viewModels { viewModelFactory }


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentMainBinding is null"
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        component.inject(this)

        observeViewModel()


        val userId = args.userId
        Log.d("MainFragment", "Received userId: $userId")
        sharedViewModel.loadUserName(args.userId)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_info_container, InfoFragment())
                .commit()
        }


        val navController = (childFragmentManager.findFragmentById(R.id.selection_nav_host_fragment)
                as NavHostFragment).navController

        val clinicInfoContainer = requireView().findViewById<View>(R.id.fragment_info_container)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            clinicInfoContainer.visibility =
                if (destination.id == R.id.selectionFragment) View.VISIBLE else View.GONE
        }


    }

    private fun observeViewModel() {
        sharedViewModel.selectionState.observe(viewLifecycleOwner) { state ->
            Log.d("MainFragment", "State changed to: $state")

            val fragmentTransaction = childFragmentManager.beginTransaction()

            when (state) {
                is SelectionState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.selectionNavHostFragment.visibility = View.GONE
                    binding.fragmentInfoContainer.visibility = View.GONE
                }
                is SelectionState.Result, is SelectionState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.selectionNavHostFragment.visibility = View.VISIBLE
                    binding.fragmentInfoContainer.visibility = View.VISIBLE
                }
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    }





