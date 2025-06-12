package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vetclinic.R
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.ProfileFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment.SettingsFragment
import com.example.vetclinic.presentation.screens.updatePasswordScreen.PasswordUpdateMode
import com.example.vetclinic.presentation.screens.updatePasswordScreen.UpdatePasswordFragment
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber


class UserFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: UserViewModel by viewModels { viewModelFactory }


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentUserBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUserBinding is null"
        )


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
        handleState()
        handleEvent()
    }


    private fun handleState() {
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UserUiState.Error -> Timber.tag(TAG)
                    .d("UserUiState.Error - заглушка для теста")

                is UserUiState.Loading -> Timber.tag(TAG)
                    .d("UserUiState.Loading - заглушка для теста")

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


    private fun handleEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> Snackbar.make(
                            binding.root,
                            event.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
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
        }

        binding.llChangePassword.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer,
                    UpdatePasswordFragment.newInstance(PasswordUpdateMode.FROM_ACCOUNT))
                addToBackStack(null)
                commit()

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
                    Snackbar.make(
                        binding.root,
                        "Пожалуйста, заполните все поля",
                        Snackbar.LENGTH_SHORT
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





















