package com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentProfileBinding
import com.example.vetclinic.di.AppComponent
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment.SettingsFragment
import com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment.petFragment.PetFragment
import com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment.userFragment.UserFragment
import com.google.android.material.button.MaterialButtonToggleGroup


class ProfileFragment : Fragment() {


    lateinit var toggleGroup: MaterialButtonToggleGroup


//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory

//    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }


    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentProfileBinding is null"
        )


    private val component: AppComponent by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        toggleGroup = binding.toggleGroup
        updateToggleGroupVisibility()


        childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, UserFragment())
            .commit()

        binding.toggleGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnUser -> loadChildFragment(UserFragment())
                    R.id.btnPet -> loadChildFragment(PetFragment())
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
            }
        )

    }

    private fun loadChildFragment(fragment: Fragment) {

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }


    fun updateToggleGroupVisibility() {
        val currentFragment = childFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is SettingsFragment) {
            toggleGroup.visibility = View.GONE
        } else {
            toggleGroup.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}



