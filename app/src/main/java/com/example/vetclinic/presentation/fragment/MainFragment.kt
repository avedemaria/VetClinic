package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentLoginBinding
import com.example.vetclinic.databinding.FragmentMainBinding
import com.example.vetclinic.presentation.VetClinicApplication


class MainFragment : Fragment() {

//    val args: MainFragmentArgs by navArgs()


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentMainBinding is null"
        )


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
//
//        val navHostFragment =
//            childFragmentManager.findFragmentById(R.id.selection_nav_host_fragment)
//                    as NavHostFragment
//
//        val navController = navHostFragment.navController

//        val navGraph = navController.navInflater.inflate(R.navigation.selection_nav_graph)
//
//        navGraph.setStartDestination(R.id.selectionFragment)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.selection_nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController
        val clinicInfoContainer = requireView().findViewById<View>(R.id.fragment_info_container)


        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_info_container, InfoFragment())
                .commit()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            clinicInfoContainer.visibility =
                if (destination.id == R.id.selectionFragment) View.VISIBLE else View.GONE
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




