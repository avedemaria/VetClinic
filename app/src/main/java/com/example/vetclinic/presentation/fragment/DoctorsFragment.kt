package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentDoctorsBinding
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.DepAndDocItemList
import com.example.vetclinic.presentation.adapter.DoctorsAdapter
import com.example.vetclinic.presentation.adapter.OnAppointmentClickListener
import com.example.vetclinic.presentation.viewmodel.DoctorUiState
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject

class DoctorsFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[DoctorViewModel::class]
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

        setUpAdapter()

        observeViewModel()


    }


    private fun setUpAdapter() {
        doctorsAdapter = DoctorsAdapter(object : OnAppointmentClickListener {
            override fun onBookButtonClick(doctor: Doctor) {
                launchDetailedDoctorInfoFragment()
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
                is DoctorUiState.Empty ->
                    Log.d("DoctorsFragment", "DoctorUiState.Empty-заглушка для теста")

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
                        listOf(DepAndDocItemList.DepartmentItem(departmentWithDoctors.department)) +
                                departmentWithDoctors.doctors.map { DepAndDocItemList.DoctorItem(it) }
                    }
            }
        }
    }


    private fun launchDetailedDoctorInfoFragment() {
        TODO()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}





