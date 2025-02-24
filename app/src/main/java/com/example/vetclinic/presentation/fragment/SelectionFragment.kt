package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.databinding.FragmentSelectionBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.MainSharedViewModel
import com.example.vetclinic.presentation.viewmodel.SelectionState
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class SelectionFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainSharedViewModel by viewModels({ requireParentFragment()
        .requireParentFragment() }) { viewModelFactory }
    private var _binding: FragmentSelectionBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentSelectionBinding is null"
        )

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        val parent = requireParentFragment()
        Log.d("FragmentCheck", "Parent: ${parent::class.java.simpleName}")


        observeViewModel()




        binding.cardViewDoctors.setOnClickListener {
            launchDoctorsFragment()
        }

        binding.cardViewServices.setOnClickListener {
            launchServicesFragment()
        }
    }


    private fun observeViewModel() {
        viewModel.selectionState.observe(requireParentFragment().viewLifecycleOwner) { state ->
            Log.d("SelectionFragment", "State changed to: $state")
            when (state) {
                is SelectionState.Error -> Toast.makeText(
                    requireContext(), "An error has occurred: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                is SelectionState.Loading -> binding.welcomeText.text = "Добро пожаловать,\nгость"
                is SelectionState.Result ->
                    binding.welcomeText.text =
                        String.format("Добро пожаловать,\n%s", state.userName)
            }
        }
    }

    private fun launchDoctorsFragment() {
        findNavController().navigate(
            SelectionFragmentDirections
                .actionSelectionFragmentToDoctorsFragment()
        )
    }

    private fun launchServicesFragment() {
        TODO()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
