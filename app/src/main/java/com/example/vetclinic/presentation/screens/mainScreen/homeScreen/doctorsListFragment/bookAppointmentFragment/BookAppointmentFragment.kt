package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.bookAppointmentFragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.vetclinic.databinding.FragmentBookAppointmentBinding
import com.example.vetclinic.domain.entities.timeSlot.Day
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.entities.timeSlot.TimeSlot
import com.example.vetclinic.utils.formatDateTime
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.adapter.timeSlotsAdapter.DaysAdapter
import com.example.vetclinic.presentation.adapter.timeSlotsAdapter.OnDayClickedListener
import com.example.vetclinic.presentation.adapter.timeSlotsAdapter.OnTimeSlotClickedListener
import com.example.vetclinic.presentation.adapter.timeSlotsAdapter.TimeSlotAdapter
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.utils.toFormattedString
import jakarta.inject.Inject


class BookAppointmentFragment : Fragment() {


    private lateinit var daysAdapter: DaysAdapter
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    private val args by navArgs<BookAppointmentFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: BookAppointmentViewModel by viewModels {
        viewModelFactory
    }


    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentBookAppointmentBinding is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
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

        val service = args.service
        val doctor = args.doctor

        viewModel.getTimeSlots(
            doctor.uid,
            service.id,
            service.duration.toString()
        )


        binding.tvDoctorInfo.text = getStyledDoctorInfo(doctor)
        binding.tvServiceInfo.text = getStyledServiceInfo(service)

//        binding.btnCreateAppointment.setOnClickListener {
//            showConfirmationDialog(service, doctor)
//        }

        setUpAdapters()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.bookAppointmentState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookAppointmentState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "An error has occurred: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.progressBar.visibility = View.GONE
                    binding.layoutContent.visibility = View.GONE
                }

                is BookAppointmentState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutContent.visibility = View.GONE
                }

                is BookAppointmentState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutContent.visibility = View.VISIBLE

                    setUpPetSpinner(state.pets)


                    //adapter RV

                    val updatedDays = state.daysWithTimeSlots.map { it.day }.map { day ->
                        day.copy(
                            isSelected = (day.id == state.selectedDay?.id),
                            date = day.date
                        )
                    }
                    daysAdapter.submitList(updatedDays)

                    val updatedTimeSlots =
                        state.filteredTimeSlots.map { timeSlot ->
                            timeSlot.copy(isSelected = (timeSlot.id == state.selectedTimeSlot?.id))
                        }

                    timeSlotAdapter.submitList(updatedTimeSlots)


                    //confirmationDialog

                    binding.btnCreateAppointment.setOnClickListener {
                        showConfirmationDialog(
                            args.service,
                            args.doctor,
                            state.selectedDay,
                            state.selectedTimeSlot,
                            state.pets
                        )
                    }

                }

                is BookAppointmentState.AppointmentAdded -> launchAppointmentFragment()
            }
        }
    }


    private fun setUpAdapters() {
        daysAdapter = DaysAdapter(object : OnDayClickedListener {
            override fun onDayClicked(day: Day) {
                viewModel.onDaySelected(day)

            }
        })

        timeSlotAdapter = TimeSlotAdapter(object : OnTimeSlotClickedListener {
            override fun onTimeSlotClicked(timeSlot: TimeSlot) {
                viewModel.onTimeSlotSelected(timeSlot)
            }
        })

        binding.rvDays.adapter = daysAdapter
        binding.rvTimeSlots.apply {
            layoutManager = GridLayoutManager(
                requireContext(), 3, HORIZONTAL,
                false
            )
            adapter = timeSlotAdapter

        }

    }


    private fun showConfirmationDialog(
        service: Service,
        doctor: Doctor,
        selectedDay: Day?,
        selectedTimeSlot: TimeSlot?,
        pets: List<Pet>
    ) {

        val selectedPetIndex = binding.spinnerPets.selectedItemPosition
        val selectedPet = pets[selectedPetIndex]
        if (selectedDay == null || selectedTimeSlot == null) {
            Toast.makeText(
                requireContext(),
                "Пожалуйста, выберите день и время",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        Log.d(TAG, "selectedDay: ${selectedDay.date}")

        AlertDialog.Builder(requireContext())
            .setTitle("Подтвердите приём: ")
            .setMessage(
                createBoldSpannableString(
                    "Дата:" to selectedDay.date.toFormattedString(),
                    "Время:" to selectedTimeSlot.startTime,
                    "${doctor.role}:" to doctor.doctorName,
                    "Услуга:" to service.serviceName,
                    "Питомец:" to selectedPet.petName,
                    "Длительность:" to "${service.duration} мин.",
                    "Цена:" to "${service.price} руб."


                )

            )
            .setPositiveButton("Подтверждаю") { dialog, _ ->
                val dateTime =
                    selectedDay.date.formatDateTime(selectedTimeSlot.startTime)
                Log.d(TAG, "dateTime: $dateTime")
                viewModel.bookAppointment(selectedPet.petId, service.id, doctor.uid, dateTime)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }


    private fun getStyledServiceInfo(service: Service): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            appendBold("Услуга: ")
            append("${service.serviceName}\n")

            appendBold("Длительность: ")
            append("${service.duration} мин.\n")

            appendBold("Цена: ")
            append("${service.price} руб.")
        }
    }

    private fun getStyledDoctorInfo(doctor: Doctor): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            appendBold("${doctor.role}: ")
            append("${doctor.doctorName}")
        }
    }


    private fun createBoldSpannableString(vararg pairs: Pair<String, String>):
            SpannableStringBuilder {

        val spannableBuilder = SpannableStringBuilder()

        pairs.forEachIndexed { index, (title, value) ->
            val titleStart = spannableBuilder.length

            spannableBuilder.append(title)
                .setSpan(
                    StyleSpan(Typeface.BOLD),
                    titleStart,
                    spannableBuilder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            spannableBuilder.append(" $value")

            if (index < pairs.size - 1) {
                spannableBuilder.append("\n")
            }
        }
        return spannableBuilder
    }

    private fun SpannableStringBuilder.appendBold(text: String) {
        val start = length
        append(text)
        setSpan(StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    private fun setUpPetSpinner(pets: List<Pet>) {

        val petNames = pets.map { it.petName }

        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, petNames
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerPets.adapter = spinnerAdapter

        binding.spinnerPets.setSelection(0)

    }


    private fun launchAppointmentFragment() {
        findNavController().navigate(
            BookAppointmentFragmentDirections.actionBookAppointmentFragmentToAppointmentFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "BookAppointmentFragment"
    }
}