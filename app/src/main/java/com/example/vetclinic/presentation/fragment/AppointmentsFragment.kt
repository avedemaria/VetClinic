package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentAppointmentsBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButtonToggleGroup
import jakarta.inject.Inject


class AppointmentsFragment : Fragment() {

//    lateinit var toggleGroup: MaterialButtonToggleGroup

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentAppointmentsBinding is null"
        )

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

//        toggleGroup = binding.toggleGroup

        loadChildFragment(CurrentAppointmentsFragment())

        binding.toggleGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnCurrentAppointments -> loadChildFragment(CurrentAppointmentsFragment())
                    R.id.btnArchivedAppointments -> loadChildFragment(ArchiveAppointmentsFragment())
                }
            }
        }
    }


    private fun loadChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentAppointmentsContainer, fragment)
            .commit()
    }

//    private fun updateToggleGroupVisibility() {
//        val currentFragment =
//            childFragmentManager.findFragmentById(R.id.fragmentAppointmentsContainer)
//
//
//
//    }
}