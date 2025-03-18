package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.HomeState
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.MainState
import com.example.vetclinic.presentation.viewmodel.MainViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import jakarta.inject.Inject


class MainFragment : Fragment() {

    private val args by navArgs<MainFragmentArgs>()

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentMainBinding is null")


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewmodel: MainViewModel by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)


        //getting userId from data store or shared preferences

        viewmodel.getUserAndPet(args.userId)

        // Получаем NavController из вложенного NavHostFragment
        val navHostFragment = childFragmentManager.findFragmentById(R.id.mainNavHostFragment)
                as NavHostFragment
        val navController = navHostFragment.navController

        // Привязываем BottomNavigationView к navController
        binding.bottomNavigationView.setupWithNavController(navController)


        binding.fab.setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.miAddAppointment
        }


        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miHome -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.miAddAppointment -> {
                    navController.navigate(R.id.doctorsFragment)
                    true
                }

                R.id.miAppointments -> {
                    navController.navigate(R.id.appointmentFragment)
                    true
                }

                else -> false
            }
        }


        observeViewModel()
    }

    private fun observeViewModel() {
        viewmodel.mainState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainState.Error -> Log.d(TAG, "Заглушка для HomeState.Error")
                MainState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.coordinatorLayout.visibility = View.GONE
                }

                is MainState.Result -> {
                    binding.progressBar.visibility = View.GONE
                    binding.coordinatorLayout.visibility = View.VISIBLE

                }
            }
        }
    }


    companion object {
        private const val TAG = "MainFragment"
    }

}






