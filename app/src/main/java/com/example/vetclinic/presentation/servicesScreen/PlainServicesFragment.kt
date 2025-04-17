package com.example.vetclinic.presentation.servicesScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.FragmentPlainServicesBinding
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.servicesAdapter.DepAndServiceItemList
import com.example.vetclinic.presentation.adapter.servicesAdapter.OnServiceClickListener
import com.example.vetclinic.presentation.adapter.servicesAdapter.ServicesWithDepAdapter
import com.example.vetclinic.presentation.ViewModelFactory
import jakarta.inject.Inject


class PlainServicesFragment : Fragment() {


    private val args by navArgs<PlainServicesFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PlainServiceViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentPlainServicesBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentPlainServicesBinding is null"
        )

    lateinit var servicesAdapter: ServicesWithDepAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlainServicesBinding.inflate(inflater, container, false)
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


        servicesAdapter = ServicesWithDepAdapter(object : OnServiceClickListener {
            override fun onServiceClick(service: Service) {
                launchBookAppointmentFragment(service, args.doctor)
            }
        })

        viewModel.getServicesByDepartmentId(args.departmentId)

        setUpAdapter()
        observeViewModel()

    }


    private fun launchBookAppointmentFragment(service: Service, doctor: Doctor) {
        findNavController().navigate(
            PlainServicesFragmentDirections
                .actionPlainServicesFragmentToBookAppointmentFragment(service, doctor)
        )
    }


    private fun setUpAdapter() {
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )
            adapter = servicesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.serviceState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is ServiceUiState.Error -> Log.d(TAG, "заглушка")
                ServiceUiState.Loading -> Log.d(TAG, "заглушка")
                is ServiceUiState.Success -> {

                    val services =
                       state.services.map { DepAndServiceItemList.ServiceItem(it) }
                    servicesAdapter.submitList(services)

                }

            }
        }
    }


    companion object {
        private const val TAG = "PlainServicesFragment"
    }

}