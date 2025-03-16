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
import com.example.vetclinic.presentation.viewmodel.UserField
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

    private val userId by lazy {
        arguments?.getString(ProfileFragment.USER_ID)
            ?: throw IllegalArgumentException("UserId is null")


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

        Log.d("UserFragment", "Received userId: $userId")

        val parentFragment = parentFragment as? ProfileFragment
        parentFragment?.toggleGroup?.visibility = View.VISIBLE

        viewModel.getUserFromRoom(userId)   //как реализовать во вьюмодели

        setUpListeners()

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

                is UserUiState.EditingField -> {
                    when (state.field) {
                        UserUiState.FieldType.NAME -> {
                            binding.tvName.visibility = View.GONE
                            binding.etName.visibility = View.VISIBLE
                            binding.btnEditName.visibility = View.GONE
                            binding.btnSaveName.visibility = View.VISIBLE
                        }

                        UserUiState.FieldType.PHONE -> {
                            binding.tvPhone.visibility = View.GONE
                            binding.tvPhoneTitle.visibility = View.GONE
                            binding.etPhone.visibility = View.VISIBLE
                            binding.btnEditPhone.visibility = View.GONE
                            binding.btnSavePhone.visibility = View.VISIBLE
                        }
                    }
                }

            }
        }
    }

    private fun setUpListeners() {
        binding.btnEditName.setOnClickListener {
            viewModel.startEditingField(UserUiState.FieldType.NAME)
        }

        binding.btnEditPhone.setOnClickListener {
            viewModel.startEditingField(UserUiState.FieldType.PHONE)
        }

        binding.btnSavePhone.setOnClickListener {
            val newPhoneNumber = binding.etPhone.text.toString()
            viewModel.updateField(userId, UserField.PHONE_NUMBER.name, newPhoneNumber)
        }

        binding.btnSaveName.setOnClickListener {
            val newUserName = binding.etName.text.toString()
            viewModel.updateField(userId, UserField.USER_NAME.name, newUserName)
        }

        binding.llSettings.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, SettingsFragment())
                addToBackStack(null)
                commit()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





