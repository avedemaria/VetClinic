package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentLoginBinding
import com.example.vetclinic.databinding.FragmentProfileBinding
import com.example.vetclinic.di.AppComponent
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButtonToggleGroup
import jakarta.inject.Inject


class ProfileFragment : Fragment() {


    lateinit var toggleGroup: MaterialButtonToggleGroup

    private val args by navArgs<ProfileFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentProfileBinding is null"
        )


    private val component: AppComponent by lazy {
        (requireActivity().application as VetClinicApplication).component
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
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
        

        toggleGroup = binding.toggleGroup
        updateToggleGroupVisibility()

        val userId = args.userId
        loadChildFragment(UserFragment(), userId)

        binding.toggleGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnUser -> loadChildFragment(UserFragment(), userId)
                    R.id.btnPet -> loadChildFragment(PetFragment(), userId)
                }
            }
        }

    }

    private fun loadChildFragment(fragment: Fragment, userId: String) {

        val bundle = Bundle().apply {
            putString(USER_ID, userId)
        }
        fragment.arguments = bundle

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


    companion object {
        const val USER_ID = "userId"
    }

}