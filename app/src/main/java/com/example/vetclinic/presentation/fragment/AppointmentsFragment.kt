package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentAppointmentsBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.AppointmentsState
import com.example.vetclinic.presentation.viewmodel.AppointmentsViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class AppointmentsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AppointmentsViewModel by viewModels { viewModelFactory }

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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        binding.toggleGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnCurrentAppointments -> loadChildFragment(CurrentAppointmentsFragment())
                    R.id.btnArchivedAppointments -> loadChildFragment(ArchivedAppointmentsFragment())
                }
            }
        }

        observeViewModel()
    }


    private fun loadChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentAppointmentsContainer, fragment)
            .commit()
    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appointmentsState.collect { state ->
                    when (state) {
                        is AppointmentsState.Error -> {
                            binding.fragmentContent.isEnabled = false
                            binding.toggleGroup.isEnabled = false
                            binding.fragmentContent.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Ошибка: ${state.message}", Toast.LENGTH_SHORT
                            ).show()
                        }

                        AppointmentsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.fragmentContent.visibility = View.GONE
                        }

                        is AppointmentsState.Success -> {
                            binding.fragmentContent.isEnabled = true
                            binding.fragmentContent.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            loadChildFragment(CurrentAppointmentsFragment())
                        }
                    }
                }
            }
        }
    }

}