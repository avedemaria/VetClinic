package com.example.vetclinic.presentation.screens.mainScreen

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.providers.ViewModelFactory
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



        val navHostFragment = childFragmentManager.findFragmentById(R.id.mainNavHostFragment)
                as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        setUpListeners(navController)
        observeViewModel()

        viewmodel.getUserIdAndFetchData()
    }


    private fun setUpListeners(navController: NavController) {

        binding.bottomNavigationView.setOnItemReselectedListener { item ->
            navController.popBackStack(navController.graph.startDestinationId, false)
        }


        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.homeFragment) {
                navController.navigate(
                    R.id.homeFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestinationId, true) 
                        .build()
                )
                true
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

            binding.fab.setOnClickListener {
                val currentDestination = navController.currentDestination?.id
                if (currentDestination != R.id.doctorsFragment) {
                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(navController.graph.startDestinationId, false)
                        .build()

                    navController.navigate(R.id.doctorsFragment, null, navOptions)
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






