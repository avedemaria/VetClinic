package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.FragmentAdminHomeBinding
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter.AdminAppointmentsAdapter
import com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter.OnBellClickListener
import com.example.vetclinic.presentation.viewmodel.AdminHomeState
import com.example.vetclinic.presentation.viewmodel.AdminHomeViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class AdminHomeFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AdminHomeViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentAdminHomeBinding is null"
        )

    private lateinit var appointmentsAdapter: AdminAppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        setUpAdapter()
        observeViewModel()


        binding.btnAdminLogOut.setOnClickListener {
            viewModel.logOut()
        }

    }


    private fun observeViewModel() {
        viewModel.adminState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AdminHomeState.Empty -> Log.d(TAG, "заглушка для Empty")
                is AdminHomeState.Error -> Log.d(TAG, "заглушка для Error")
                AdminHomeState.Loading -> Log.d(TAG, "заглушка для Loading")
                is AdminHomeState.Success -> appointmentsAdapter.submitList(state.appointments)
                is AdminHomeState.LoggedOut -> launchLoginFragment()
            }
        }
    }


    private fun setUpAdapter() {
        appointmentsAdapter = AdminAppointmentsAdapter(object : OnBellClickListener {
            override fun onBellClicked(appointment: AppointmentWithDetails) {
                viewModel.updateAppointmentStatus(appointment.copy(isConfirmed = true))
            }
        })

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL,
                false
            )
            adapter = appointmentsAdapter
        }
    }


    private fun launchLoginFragment() {
        findNavController().navigate(AdminHomeFragmentDirections.actionAdminHomeFragment2ToLoginFragment())
    }


    companion object {
        private const val TAG = "AdminHomeFragment"
    }


}