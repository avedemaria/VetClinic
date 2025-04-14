package com.example.vetclinic.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentArchiveAppointmentsBinding
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.AppointmentsAdapter
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.OnAppointmentMenuClickListener
import com.example.vetclinic.presentation.viewmodel.SharedAppointmentsState
import com.example.vetclinic.presentation.viewmodel.SharedAppointmentsViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.example.vetclinic.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar


class ArchivedAppointmentsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SharedAppointmentsViewModel by viewModels({ requireParentFragment() }) { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentArchiveAppointmentsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentArchiveAppointmentsBinding is null"
        )


    private lateinit var appointmentsAdapter: AppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArchiveAppointmentsBinding.inflate(
            inflater, container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)
        Log.d(TAG, "OnCreateView")
        setUpListeners()
        setUpAdapter()
        observeViewModel()
        Log.d(TAG, "$viewModel")

    }


    private fun setUpListeners() {

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    findNavController().popBackStack(R.id.homeFragment, false)
//                }
//            })


        binding.btnCalendar.setOnClickListener {
            Log.d(TAG, "calendarClicked")
            showDatePickerDialog()
        }
    }


    private fun setUpAdapter() {

        appointmentsAdapter = AppointmentsAdapter(object : OnAppointmentMenuClickListener {
            override fun onAppointmentMenuClicked(appointment: AppointmentWithDetails) {
                Log.d(TAG, "заглушка для listener")
            }
        })

        binding.rvArchivedAppointments.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL,
                false
            )

            adapter = appointmentsAdapter
        }
    }


    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appointmentsState.collect { state ->
                    when (state) {
                        SharedAppointmentsState.Empty -> {
                            binding.rvArchivedAppointments.visibility = View.GONE
                            binding.tvEmptyAppointments.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }

                        is SharedAppointmentsState.Error -> {
                            binding.archivedAppointmentContent.isEnabled = false
                            binding.progressBar.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                            ).show()
                        }

                        SharedAppointmentsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyAppointments.visibility = View.GONE
                            binding.archivedAppointmentContent.isEnabled = false
                        }

                        is SharedAppointmentsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.archivedAppointmentContent.isEnabled = true
                            binding.rvArchivedAppointments.visibility = View.VISIBLE


                            val selectedDate = state.selectedDate
                            val appointments = state.appointments

                            val filteredAppointments =
                                filterAppointments(appointments, selectedDate)

                            if (filteredAppointments.isEmpty()) {
                                binding.rvArchivedAppointments.visibility = View.GONE
                                binding.tvEmptyAppointments.visibility = View.VISIBLE
                            } else {
                                binding.rvArchivedAppointments.visibility = View.VISIBLE
                                binding.tvEmptyAppointments.visibility = View.GONE
                            }
                            Log.d(TAG, "filtered archived appointments: $filteredAppointments")

                            appointmentsAdapter.submitList(filteredAppointments)

                        }
                    }
                }
            }
        }
    }


    private fun filterAppointments(
        appointments: List<AppointmentWithDetails>,
        selectedDate: LocalDate?,
    ): List<AppointmentWithDetails> {
        return appointments.filter { it.isArchived }.filter { appointment ->
            val appointmentDate =
                appointment.dateTime.toLocalDateOrNull("yyyy-MM-dd'T'HH:mm:ss")
            if (appointmentDate != null && selectedDate != null) {
                appointmentDate.isEqual(selectedDate)
            } else {
                true
            }
        }.sortedByDescending {
            it.dateTime
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        Log.d(TAG, "showDatePickerDialog called")

        val lastCompletedDate = viewModel.getLastCompletedAppointmentDate()

        if (lastCompletedDate == null) {
            Toast.makeText(
                requireContext(), "No appointments found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        calendar.set(
            lastCompletedDate.year,
            lastCompletedDate.monthValue - 1,
            lastCompletedDate.dayOfMonth
        )

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.setUpSelectedDate(selectedDate)//устанавливаем значение selected day во вьюмодель из диалога, далее в success
                // можно будет его использовать для отображения нужного списка
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val lastAppointmentMillis = lastCompletedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        datePickerDialog.datePicker.maxDate = lastAppointmentMillis
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "OnDestroyView")
        _binding = null
    }


//    override fun onResume() {
//        super.onResume()
//        viewModel.subscribeToAppointmentChanges()
//        Log.d(TAG, "subscribed to websocket")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Отписка от WebSocket
//        Log.d(TAG, "Unsubscribed from ArchivedAppointmentsFragment")
//        viewModel.unsubscribeFromChanges()
//    }


    companion object {
        private const val TAG = "ArchivedAppointmentsFragment"
    }
}






