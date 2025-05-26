package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.ServicesListFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.FragmentServicesBinding
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.adapter.servicesAdapter.DepAndServiceItemList
import com.example.vetclinic.presentation.adapter.servicesAdapter.OnServiceClickListener
import com.example.vetclinic.presentation.adapter.servicesAdapter.ServicesWithDepAdapter
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject


class ServicesWithDepFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private val viewModel: ServiceWithDepViewModel by viewModels { viewModelFactory }


    private var _binding: FragmentServicesBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentServicesBinding is null"
        )

    private lateinit var servicesWithDepAdapter: ServicesWithDepAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()  // Возврат к предыдущему фрагменту
                }
            })


        setUpAdapter()

        observeViewModel()


    }

    private fun setUpAdapter() {
        servicesWithDepAdapter = ServicesWithDepAdapter(object : OnServiceClickListener {
            override fun onServiceClick(service: Service) {
                launchDetailedServiceInfoFragment()
            }
        })

        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL, false
            )
            adapter = servicesWithDepAdapter
        }

    }


    private fun observeViewModel() {

        viewModel.serviceState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ServiceWithDepUiState.Empty ->
                    Log.d(TAG, "ServiceWithDepUiState.Empty-заглушка для теста")

                is ServiceWithDepUiState.Error ->   Snackbar.make(binding.root,
                    "Oшибка: ${state.message}",
                    Snackbar.LENGTH_SHORT
                ).show()

                is ServiceWithDepUiState.Loading -> Log.d(
                    TAG,
                    "ServiceWithDepUiState.Loading - заглушка для теста"
                )

                is ServiceWithDepUiState.Success -> {
                    val serviceItems = state.services.flatMap { departmentWithServices ->
                        listOf(
                            DepAndServiceItemList.DepartmentItem(
                                departmentWithServices
                                    .department.name
                            )
                        ) + departmentWithServices.services.map {
                            DepAndServiceItemList.ServiceItem(it)
                        }
                    }
                    servicesWithDepAdapter.submitList(serviceItems)
                }
            }
        }
    }


    private fun launchDetailedServiceInfoFragment() {
        Log.d(TAG, "fragment launched")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ServicesWithDepFragment"
    }


}
