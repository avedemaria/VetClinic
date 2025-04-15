package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vetclinic.databinding.FragmentHomeBinding
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.HomeState
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment() {


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: HomeViewModel by activityViewModels { viewModelFactory }


    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentHomeBinding is null"
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        observeViewModel()


        binding.profileButton.setOnClickListener {
            launchProfileFragment()
        }

        binding.cardViewDoctors.setOnClickListener {
            launchDoctorsFragment()
        }

        binding.cardViewServices.setOnClickListener {
            launchServicesFragment()
        }

        Log.d("HomeFragment", "onViewCreated")

        viewModel.getUserIdAndLoadUserName()

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (isAdded) {
                    setupDialogFlow()
                    viewModel.checkShouldShowDialog(requireContext())
                }
            }
        })

    }


    private fun setupDialogFlow() {
        viewModel.showDialogEvent.onEach {
            Log.d(TAG, "received event: $it")
            showBatteryOptimizationDialog()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun observeViewModel() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            Log.d("HomeFragment", "State changed to: $state")
            when (state) {
                is HomeState.Error -> {
                    Toast.makeText(
                        requireContext(), "An error has occurred: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is HomeState.Loading -> {
                    binding.welcomeText.text = "Добро пожаловать,\nгость"
                }

                is HomeState.Result -> {
                    binding.welcomeText.text =
                        String.format("Добро пожаловать,\n%s", state.userName)
                }

            }
        }
    }


    private fun launchDoctorsFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDoctorsFragment()

        )
    }

    private fun launchServicesFragment() {
        findNavController().navigate(
            HomeFragmentDirections
                .actionHomeFragmentToServicesWithDepFragment()
        )
    }

    private fun launchProfileFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToProfileFragment()
        )
    }


    private fun showBatteryOptimizationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Показ уведомлений за час до приёма")
            .setMessage(
                "Чтобы получать напоминания за час до приёма, отключите оптимизацию батареи для приложения.\n\n" +
                        "Нажмите \"Перейти в настройки\", затем найдите пункт \"Аккумулятор\" и отключите ограничение."
            )
            .setPositiveButton("Перейти в настройки", null)
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Больше не показывать", null)
            .create()

        dialog.setOnShowListener {
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

            positive.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:" + requireContext().packageName)
                }
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    requireActivity().startActivity(intent)
                }
                dialog.dismiss()
            }

            neutral.setOnClickListener {
                viewModel.disableDialogForever()
                dialog.dismiss()
            }

            negative.setOnClickListener {
                dialog.dismiss()
            }

            // Выравниваем кнопки по центру, если хочешь
            val layoutParams = positive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 1f
            positive.layoutParams = layoutParams
            negative.layoutParams = layoutParams
            neutral.layoutParams = layoutParams
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "HomeFragment"
    }

}













