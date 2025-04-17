package com.example.vetclinic.presentation.appointmentsScreen.currentAppointmentsScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.FragmentCurrentAppointmentsBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.AppointmentsAdapter
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.OnAppointmentMenuClickListener
import com.example.vetclinic.presentation.appointmentsScreen.SharedAppointmentsState
import com.example.vetclinic.presentation.appointmentsScreen.SharedAppointmentsViewModel
import com.example.vetclinic.presentation.ViewModelFactory
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class CurrentAppointmentsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SharedAppointmentsViewModel by viewModels({ requireParentFragment() })
    { viewModelFactory }

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
        savedInstanceState: Bundle?,
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

        appointmentsAdapter = AppointmentsAdapter(object : OnAppointmentMenuClickListener {
            override fun onAppointmentMenuClicked(appointment: AppointmentWithDetails) {
                showCancelAppointmentDialog(appointment)
                Log.d(TAG, "updated Appointment1: $appointment")

            }
        })

        binding.rvCurrentAppointments.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL,
                false
            )

            adapter = appointmentsAdapter
        }


    }


    private fun showCancelAppointmentDialog(appointment: AppointmentWithDetails) {

        AlertDialog.Builder(requireContext())
            .setTitle("Подтвердите отмену приёма")
            .setMessage("Вы уверены, что хотите отменить приём?")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { _, _ ->
                viewModel.updateAppointmentStatus(
                    appointment.copy(status = AppointmentStatus.CANCELLED, isArchived = true)
                )
            }
            .create()
            .show()

        Log.d(TAG, "updated Appointment2: $appointment")


    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appointmentsState.collect { state ->
                    when (state) {
                        SharedAppointmentsState.Empty -> {
                            binding.rvCurrentAppointments.visibility = View.GONE
                            binding.tvEmptyAppointments.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }

                        is SharedAppointmentsState.Error -> {
                            binding.currentAppointmentContent.isEnabled = false
                            binding.tvEmptyAppointments.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                            ).show()
                        }

                        SharedAppointmentsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.currentAppointmentContent.isEnabled = false
                        }

                        is SharedAppointmentsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.currentAppointmentContent.isEnabled = true

                            val appointments = state.appointments
                            val filteredAppointments =
                                appointments.filter { !it.isArchived }.sortedBy { it.dateTime }

                            appointmentsAdapter.submitList(filteredAppointments)
                        }
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CurrentAppointmentsFragment"
    }

}