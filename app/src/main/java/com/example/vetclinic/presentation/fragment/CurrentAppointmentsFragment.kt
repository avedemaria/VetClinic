package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentCurrentAppointmentsBinding
import com.example.vetclinic.databinding.FragmentDetailedDoctorInfoBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.AppointmentsAdapter
import com.example.vetclinic.presentation.viewmodel.CurrentAppointmentsState
import com.example.vetclinic.presentation.viewmodel.CurrentAppointmentsViewModel
import com.example.vetclinic.presentation.viewmodel.PlainServiceViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class CurrentAppointmentsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CurrentAppointmentsViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentCurrentAppointmentsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentCurrentAppointmentsBinding is null"
        )


    private lateinit var appointmentsAdapter: AppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentAppointmentsBinding.inflate(
            inflater, container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        setUpAdapter()

        observeViewModel()


    }


    private fun setUpAdapter() {

        appointmentsAdapter = AppointmentsAdapter()

        binding.rvCurrentAppointments.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL,
                false
            )

            adapter = appointmentsAdapter
        }
    }


    private fun observeViewModel() {
        viewModel.appointmentState.observe(viewLifecycleOwner) { state ->
            when (state) {
                CurrentAppointmentsState.Empty -> {
                    binding.rvCurrentAppointments.visibility = View.GONE
                    binding.tvEmptyAppointments.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }

                is CurrentAppointmentsState.Error -> {
                    binding.currentAppointmentContent.isEnabled = false
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                CurrentAppointmentsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.currentAppointmentContent.isEnabled = false
                }

                is CurrentAppointmentsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.currentAppointmentContent.isEnabled = true

                    appointmentsAdapter.submitList(state.appointments)
                }
            }

        }
    }


    companion object {
        private const val TAG = "CurrentAppointmentsFragment"
    }
}