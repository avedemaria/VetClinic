package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentDoctorsBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.UserField
import com.example.vetclinic.presentation.viewmodel.UserUiState
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        val parentFragment = parentFragment as? ProfileFragment
        parentFragment?.toggleGroup?.visibility = View.VISIBLE


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
                    TAG,
                    "UserUiState.Loading - заглушка для теста"
                )

                is UserUiState.Success -> {
                    binding.tvName.text = "${state.user.userName} ${state.user.userLastName}"
                    binding.tvEmail.text = state.user.email
                    binding.tvPhone.text = state.user.phoneNumber

                    binding.tvPhone.visibility = View.VISIBLE
                    binding.etPhone.visibility = View.GONE
                    binding.btnEditPhone.visibility = View.VISIBLE
                    binding.btnSavePhone.visibility = View.GONE

                    binding.btnEditName.setOnClickListener {
                        changeUserNameDialog(state.user)
                    }
                }

                is UserUiState.EditingField -> {
                    when (state.field) {
                        UserUiState.FieldType.PHONE -> {
                            binding.tvPhone.visibility = View.GONE
                            binding.etPhone.visibility = View.VISIBLE
                            binding.etPhone.setText(binding.tvPhone.text.toString())
                            binding.btnEditPhone.visibility = View.GONE
                            binding.btnSavePhone.visibility = View.VISIBLE

                        }
                    }
                }

            }
        }
    }


    private fun setUpListeners() {


        binding.btnEditPhone.setOnClickListener {
            viewModel.startEditingField(UserUiState.FieldType.PHONE)
        }

        binding.btnSavePhone.setOnClickListener {
            val newPhone = binding.etPhone.text.toString().trim()
            viewModel.updateField(UserField.PHONE_NUMBER.name, newPhone)
            viewModel.finishEditing()
        }


        binding.llSettings.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, SettingsFragment())
                addToBackStack(null)
                commit()
            }

            binding.llSettings.setOnClickListener {
                Snackbar.make(binding.root, "В разработке", Snackbar.LENGTH_SHORT).show()
            }

        }
    }


    private fun changeUserNameDialog(user: User) {


        val etName = EditText(requireContext()).apply {
            setText(user.userName)
            hint = "Введите имя"
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(32, 16, 32, 16)
        }

        val etLastName = EditText(requireContext()).apply {
            setText(user.userLastName)
            hint = "Введите фамилию"
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(32, 16, 32, 16)
        }

        val spaceView = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (16 * resources.displayMetrics.density).toInt()
            )
        }


        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
            addView(etName)
            addView(spaceView)
            addView(etLastName)
        }


        AlertDialog.Builder(requireContext())
            .setTitle("Введите новые данные: ")
            .setView(container)
            .setPositiveButton("ОК") { dialog, _ ->
                val newName = etName.text.toString().trim()
                val newLastName = etLastName.text.toString().trim()


                if ((newName.isNotBlank() && newName != user.userName) ||
                    (newLastName.isNotBlank() && newLastName != user.userLastName)
                ) {
                    val updatedUser =
                        user.copy(
                            userName = newName.replaceFirstChar { it.uppercase() },
                            userLastName = newLastName.replaceFirstChar { it.uppercase() })
                    viewModel.updateUser(updatedUser)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()

            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "UserFragment"
    }
}





















