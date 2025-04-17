package com.example.vetclinic.presentation.mainScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.ViewModelFactory
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import jakarta.inject.Inject


class MainFragment : Fragment() {


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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })


        // Получаем NavController из вложенного NavHostFragment
        val navHostFragment = childFragmentManager.findFragmentById(R.id.mainNavHostFragment)
                as NavHostFragment
        val navController = navHostFragment.navController

        // Привязываем BottomNavigationView к navController
        binding.bottomNavigationView.setupWithNavController(navController)

        setUpListeners(navController)
        observeViewModel()

        viewmodel.getUserIdAndFetchData()
    }


    private fun setUpListeners(navController: NavController) {

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
//                    binding.bottomNavigationView.itemRippleColor =
//                        ColorStateList.valueOf(Color.TRANSPARENT)
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


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.detailedDoctorInfoFragment) {
                binding.bottomNavigationView.visibility = View.GONE
                binding.fab.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.fab.visibility = View.VISIBLE
                setUpBottomNavViewAnimation()

            }

        }
    }

    private fun setUpBottomNavViewAnimation() {
        val bottomNavLayoutParams =
            binding.bottomNavigationView.layoutParams as CoordinatorLayout.LayoutParams
        val bottomNavBehavior = bottomNavLayoutParams.behavior as? HideBottomViewOnScrollBehavior
        bottomNavBehavior?.slideUp(binding.bottomNavigationView)

        val fabLayoutParams = binding.fab.layoutParams as CoordinatorLayout.LayoutParams
        val fabBehavior = fabLayoutParams.behavior as? HideBottomViewOnScrollBehavior
        fabBehavior?.slideUp(binding.fab)
    }

    private fun observeViewModel() {
        viewmodel.mainState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainState.Error -> Log.d(TAG, "Заглушка для MainState.Error")
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






