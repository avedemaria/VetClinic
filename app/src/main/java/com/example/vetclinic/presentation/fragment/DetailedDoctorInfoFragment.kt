package com.example.vetclinic.presentation.fragment

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentDetailedDoctorInfoBinding
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.DoctorServicesAdapter
import com.example.vetclinic.presentation.viewmodel.PlainServiceViewModel
import com.example.vetclinic.presentation.viewmodel.ServiceUiState
import com.example.vetclinic.presentation.viewmodel.ServiceWithDepUiState
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject

class DetailedDoctorInfoFragment : Fragment() {


    private val args: DetailedDoctorInfoFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PlainServiceViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentDetailedDoctorInfoBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentDetailedDoctorInfoBinding is null"
        )

    private lateinit var servicesAdapter: DoctorServicesAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedDoctorInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Doctor", "${args.doctor}")


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()  // Возврат к предыдущему фрагменту
                }
            })



        setUpAdapter()

        observeViewModel()


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


//        binding.btnBook.setOnClickListener {
//            launchPlainServicesFragment(args.doctor)
//        }
//
//        binding.btnToAllServices.setOnClickListener {
//            launchPlainServicesFragment(args.doctor)
//        }
//

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
        viewModel.serviceState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ServiceUiState.Empty -> Log.d(TAG, "DoctorUiState.Empty-заглушка для теста")
                is ServiceUiState.Error -> Toast.makeText(
                    requireContext(),
                    "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                ServiceUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentContainer.visibility = View.GONE
                }

                is ServiceUiState.Success -> {

                    binding.progressBar.visibility = View.GONE
                    binding.contentContainer.visibility = View.VISIBLE

                    binding.allServicesButtonContainer.visibility = View.VISIBLE

                    val groupedServices =
                        state.services.filter { it.departmentId == args.doctor.departmentId }


                    servicesAdapter.setServices(
                        groupedServices,
                        binding.allServicesButtonContainer
                    )
                }
            }
        }
    }


//    private fun launchPlainServicesFragment(doctor: Doctor) {
//        findNavController().navigate(
//            DetailedDoctorInfoFragmentDirections
//                .actionDetailedDoctorInfoFragmentToPlainServicesFragment(doctor)
//        )
//    }

    companion object {
        private const val TAG = "DetailedDoctorInfoFragment"
    }
}