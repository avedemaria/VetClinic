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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainFragment : Fragment() {

    private val args by navArgs<MainFragmentArgs>()

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentMainBinding is null")

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


        val sharedPrefs = requireContext().getSharedPreferences(USER_ID, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(USER_ID, args.userId).apply()

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
//            OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                requireActivity().finish()
//            }
//        })


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
    }


    companion object {
        const val USER_ID = "userId"
    }
}






