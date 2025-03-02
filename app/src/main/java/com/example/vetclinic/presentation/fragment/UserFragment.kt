package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentDoctorsBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.UserUiState
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class UserFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: UserViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentUserBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUserBinding is null"
        )

    private val component by lazy {
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
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString(ProfileFragment.USER_ID)
            ?: throw IllegalArgumentException("UserId is null")

        Log.d("UserFragment", "Received userId: $userId")

        viewModel.getUserFromRoom(userId)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UserUiState.Error -> Toast.makeText(
                    requireContext(),
                    "The error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                is UserUiState.Loading -> Log.d(
                    "UsersFragment",
                    "UserUiState.Loading - заглушка для теста"
                )

                is UserUiState.Success -> {
                    binding.tvName.text = "${state.user.userName} ${state.user.userLastName}"
                    binding.tvEmail.text = state.user.email
                    binding.tvPhone.text = state.user.phoneNumber
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
