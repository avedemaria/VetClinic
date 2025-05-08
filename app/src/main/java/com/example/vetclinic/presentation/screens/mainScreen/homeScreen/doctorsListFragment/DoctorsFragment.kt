package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment

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
import com.example.vetclinic.databinding.FragmentDoctorsBinding
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.adapter.doctorsAdapter.DepAndDocItemList
import com.example.vetclinic.presentation.adapter.doctorsAdapter.DoctorsAdapter
import com.example.vetclinic.presentation.adapter.doctorsAdapter.OnAppointmentClickListener
import com.example.vetclinic.presentation.providers.ViewModelFactory
import jakarta.inject.Inject

class DoctorsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DoctorViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentDoctorsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentDoctorsBinding is null"
        )

    private lateinit var doctorsAdapter: DoctorsAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })



        setUpAdapter()

        observeViewModel()


    }


    private fun setUpAdapter() {
        doctorsAdapter = DoctorsAdapter(object : OnAppointmentClickListener {
            override fun onBookButtonClick(doctor: Doctor) {
                launchDetailedDoctorInfoFragment(doctor, doctor.departmentId)
            }
        })

        binding.rvDoctors.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL, false
            )
            adapter = doctorsAdapter
        }

    }


    private fun observeViewModel() {

        viewModel.doctorState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is DoctorUiState.Error -> Toast.makeText(
                    requireContext(),
                    "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                is DoctorUiState.Loading -> Log.d(
                    "DoctorsFragment",
                    "DoctorUiState.Loading - заглушка для теста"
                )

                is DoctorUiState.Success ->
                    doctorsAdapter.items = state.doctors.flatMap { departmentWithDoctors ->
                        listOf(DepAndDocItemList.DepartmentItem(departmentWithDoctors.department.name)) +
                                departmentWithDoctors.doctors.map { DepAndDocItemList.DoctorItem(it) }
                    }
            }
        }
    }


    private fun launchDetailedDoctorInfoFragment(doctor: Doctor, departmentId:String) {
        findNavController()
            .navigate(
                DoctorsFragmentDirections.actionDoctorsFragmentToDetailedDoctorInfoFragment(
                    doctor, departmentId
                )
            )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "DoctorsFragment"
    }

}





