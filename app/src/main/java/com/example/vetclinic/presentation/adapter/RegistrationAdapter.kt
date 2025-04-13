package com.example.vetclinic.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vetclinic.presentation.fragment.PetRegistrationForm
import com.example.vetclinic.presentation.fragment.UserRegistrationForm

class RegistrationAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int = REGISTRATION_FORM_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserRegistrationForm()
            1 -> PetRegistrationForm()
            else -> throw IllegalArgumentException("Invalid step")
        }
    }


    companion object {
        private const val REGISTRATION_FORM_COUNT = 2
    }
}