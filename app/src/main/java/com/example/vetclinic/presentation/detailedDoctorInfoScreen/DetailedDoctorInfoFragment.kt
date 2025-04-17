package com.example.vetclinic.presentation.detailedDoctorInfoScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentDetailedDoctorInfoBinding
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.doctorsAdapter.DoctorServicesAdapter
import com.example.vetclinic.presentation.ViewModelFactory
import jakarta.inject.Inject

class DetailedDoctorInfoFragment : Fragment() {


    private val args: DetailedDoctorInfoFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DetailedDoctorInfoViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentDetailedDoctorInfoBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentDetailedDoctorInfoBinding is null"
        )

    private lateinit var servicesAdapter: DoctorServicesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailedDoctorInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)


        Log.d("Doctor", "${args.doctor}")


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()  // Возврат к предыдущему фрагменту
                }
            })

        viewModel.getServicesByDepartmentId(args.departmentId)

        setUpAdapter()

        observeViewModel()




        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.btnBook.setOnClickListener {
            launchPlainServicesFragment(args.doctor, args.departmentId)
        }

        binding.btnToAllServices.setOnClickListener {
            launchPlainServicesFragment(args.doctor, args.departmentId)
        }


        binding.doctorName.text = args.doctor.doctorName
        binding.doctorSpecialization.text = args.doctor.role

        Glide.with(this)
            .load(args.doctor.photoUrl)
            .placeholder(R.drawable.placeholder_circle)
            .circleCrop()
            .into(binding.doctorImage)


    }


    private fun setUpAdapter() {
        servicesAdapter = DoctorServicesAdapter(binding.servicesContainer, layoutInflater)
    }


    private fun observeViewModel() {
        viewModel.detailedDoctorState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailedDoctorState.Error -> Toast.makeText(
                    requireContext(),
                    "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                DetailedDoctorState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentContainer.visibility = View.GONE
                }

                is DetailedDoctorState.Success -> {

                    binding.progressBar.visibility = View.GONE
                    binding.contentContainer.visibility = View.VISIBLE

                    binding.allServicesButtonContainer.visibility = View.VISIBLE


                    servicesAdapter.setServices(
                        state.services,
                        binding.allServicesButtonContainer
                    )
                }
            }
        }
    }


    private fun launchPlainServicesFragment(doctor: Doctor, departmentId: String) {
        findNavController().navigate(
            DetailedDoctorInfoFragmentDirections
                .actionDetailedDoctorInfoFragmentToPlainServicesFragment(doctor, departmentId)
        )
    }

    companion object {
        private const val TAG = "DetailedDoctorInfoFragment"
    }
}