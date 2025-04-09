package com.example.vetclinic.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar


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
        Log.d(TAG, "onCreateView")
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


        binding.btnCalendar.setOnClickListener {
            showDatePickerDialog()
        }

    }


    private fun observeViewModel() {
        viewModel.adminState.onEach { state ->
            Log.d(TAG, "Received state: $state")
            when (state) {
                is AdminHomeState.Empty -> handleEmptyState()

                is AdminHomeState.Error -> {
                    handeErrorState(state)
                }

                is AdminHomeState.Loading -> handleLoadingState()

                is AdminHomeState.Success -> {
                    Log.d(TAG, "State Success, appointments: ${state.appointments}")
                    handleSuccessState(state)
                }


                is AdminHomeState.LoggedOut -> launchLoginFragment()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun setUpAdapter() {
        appointmentsAdapter = AdminAppointmentsAdapter(object : OnBellClickListener {
            override fun onBellClicked(appointment: AppointmentWithDetails) {
//                viewModel.updateAppointmentStatus(appointment.copy(isConfirmed = true))
                Log.d(TAG, "заглушка")
            }
        })

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL,
                false
            )
            Log.d(TAG, "Setting up adapter")
            adapter = appointmentsAdapter
        }
    }


    private fun handleEmptyState() {
        setVisibility(
            progressBarVisible = false,
            rvAppointmentsVisible = false,
            appointmentContentEnabled = true,
            emptyAppointmentsVisible = true
        )
    }

    private fun handleSuccessState(state: AdminHomeState.Success) {
        setVisibility(
            progressBarVisible = false,
            rvAppointmentsVisible = true,
            appointmentContentEnabled = true,
            emptyAppointmentsVisible = false
        )
        appointmentsAdapter.submitData(viewLifecycleOwner.lifecycle, state.appointments)
    }

    private fun handeErrorState(state: AdminHomeState.Error) {
        setVisibility(
            progressBarVisible = false,
            rvAppointmentsVisible = true,
            appointmentContentEnabled = false,
            emptyAppointmentsVisible = false
        )

        showToast("Возникла ошибка: ${state.message}")
    }

    private fun handleLoadingState() {
        setVisibility(
            progressBarVisible = true,
            rvAppointmentsVisible = false,
            appointmentContentEnabled = false,
            emptyAppointmentsVisible = false
        )
    }

    private fun setVisibility(
        progressBarVisible: Boolean,
        rvAppointmentsVisible: Boolean,
        appointmentContentEnabled: Boolean,
        emptyAppointmentsVisible: Boolean,
    ) {
        binding.progressBar.visibility = if (progressBarVisible) View.VISIBLE else View.GONE
        binding.rvAppointments.visibility = if (rvAppointmentsVisible) View.VISIBLE else View.GONE
        binding.appointmentContent.isEnabled = appointmentContentEnabled
        binding.tvEmptyAppointments.visibility =
            if (emptyAppointmentsVisible) View.VISIBLE else View.GONE
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val currentDate = viewModel.getCurrentDate()

        if (currentDate == null) {
//            Snackbar.make(binding.root, "Нет доступных приемов", Snackbar.LENGTH_SHORT).show()
            Toast.makeText(
                requireContext(), "No appointments found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        calendar.set(
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth
        )

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month, day)
                viewModel.setUpSelectedDate(selectedDate)
            },

            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            val calendarLimit =
                currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            datePicker.maxDate = calendarLimit
        }.show()

    }

    private fun launchLoginFragment() {
        findNavController().navigate(AdminHomeFragmentDirections.actionAdminHomeFragment2ToLoginFragment())
    }


    override fun onDestroyView() {
        Log.d("AdminHomeFragment", "onDestroyView")
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "AdminHomeFragment"
    }


}