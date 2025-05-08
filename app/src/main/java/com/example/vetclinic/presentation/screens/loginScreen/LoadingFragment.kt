package com.example.vetclinic.presentation.screens.loginScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentLoadingBinding
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.presentation.providers.ViewModelFactory
import jakarta.inject.Inject


class LoadingFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoadingViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentLoadingBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentLoadingBinding is null"
        )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingState.Error -> {
                    Log.d(TAG, "заглушка для Error")
                    findNavController().navigate(
                        LoadingFragmentDirections.actionLoadingFragmentToLoginFragment()
                    )
                }

                LoadingState.Loading -> Log.d(TAG, "заглушка для Loading")
                is LoadingState.Result -> {
                    val userId = state.userId
                    val userRole = state.userRole

                    if (userId.isEmpty()) {
                        // No user ID means not logged in, go to login
                        findNavController().navigate(
                            LoadingFragmentDirections.actionLoadingFragmentToLoginFragment()
                        )
                    } else if (userRole.isEmpty()) {
                        // User ID exists but role is empty - direct to login to refresh role
                        // First clear user session to avoid loop
                        viewModel.clearUserSession()
                        findNavController().navigate(
                            LoadingFragmentDirections.actionLoadingFragmentToLoginFragment()
                        )
                    } else {
                        when (userRole) {
                            ADMIN -> if (userId.isNotEmpty()) {
                                findNavController().navigate(
                                    LoadingFragmentDirections
                                        .actionLoadingFragmentToAdminHomeFragment()
                                )
                            } else {
                                findNavController().navigate(
                                    LoadingFragmentDirections
                                        .actionLoadingFragmentToLoginFragment()
                                )
                            }

                            USER -> if (userId.isNotEmpty()) {
                                findNavController().navigate(
                                    LoadingFragmentDirections.actionLoadingFragmentToMainFragment()
                                )
                            } else {
                                viewModel.clearUserSession()
                                findNavController().navigate(
                                    LoadingFragmentDirections.actionLoadingFragmentToLoginFragment()
                                )
                            }
                        }
                    }
                }
            }

        }
    }


    companion object {
        private const val TAG = "LoadingFragment"
        private const val ADMIN = "admin"
        private const val USER = "user"
    }


}