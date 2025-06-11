package com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.archivedAppointmentsFragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.FragmentArchiveAppointmentsBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.AppointmentsAdapter
import com.example.vetclinic.presentation.adapter.appointmentsAdapter.OnAppointmentMenuClickListener
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.SharedAppointmentsState
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.SharedAppointmentsViewModel
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.utils.toLocalDateOrNull
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber
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
        setUpListeners()
        setUpAdapter()
        observeViewModel()

    }


    private fun setUpListeners() {


        binding.btnCalendar.setOnClickListener {
            showDatePickerDialog()
        }
    }


    private fun setUpAdapter() {

        appointmentsAdapter = AppointmentsAdapter(object : OnAppointmentMenuClickListener {
            override fun onAppointmentMenuClicked(appointment: AppointmentWithDetails) {
                Timber.tag(TAG).d("заглушка для listener")
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
        handleState()
        handleEvent()
    }

    private fun handleState () {
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
                        }

                        SharedAppointmentsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyAppointments.visibility = View.GONE
                            binding.archivedAppointmentContent.isEnabled = false
                        }

                        is SharedAppointmentsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.archivedAppointmentContent.isEnabled = true
                            binding.archivedAppointmentContent.visibility = View.VISIBLE
                            binding.rvArchivedAppointments.visibility = View.VISIBLE
                            binding.tvEmptyAppointments.visibility = View.GONE


                            val selectedDate = state.selectedDate
                            val appointments = state.appointments

                            val filteredAppointments =
                                filterAppointments(appointments, selectedDate)

                            if (filteredAppointments.isEmpty()) {
                                binding.rvArchivedAppointments.visibility = View.GONE
                                binding.tvEmptyAppointments.visibility = View.VISIBLE
                                binding.btnCalendar.visibility = View.VISIBLE
                            } else {
                                binding.rvArchivedAppointments.visibility = View.VISIBLE
                                binding.tvEmptyAppointments.visibility = View.GONE
                                binding.btnCalendar.visibility = View.VISIBLE
                            }

                            appointmentsAdapter.submitList(filteredAppointments)

                        }
                    }
                }
            }
        }
    }

    private fun handleEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> Snackbar.make(
                            binding.root,
                            event.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
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
        _binding = null
    }


    companion object {
        private const val TAG = "ArchivedAppointmentsFragment"
    }
}






