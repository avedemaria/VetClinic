package com.example.vetclinic.presentation

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vetclinic.R
import com.example.vetclinic.presentation.fragment.LoginFragmentDirections
import com.example.vetclinic.presentation.fragment.UpdatePasswordFragmentDirections
import com.example.vetclinic.presentation.viewmodel.LoginState
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.ResetPasswordViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class MainActivity : AppCompatActivity() {

    private val component by lazy {
        (application as VetClinicApplication).component
    }

    private lateinit var navController: NavController

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        component.inject(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController


        if (savedInstanceState == null) {
            handleDeepLink(intent)
        }
    }


    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        handleDeepLink(intent)
    }


    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            Log.d("DeepLink", "Received URI: $uri")

            if (uri.host == "reset-password") {
                val fullUri = uri.toString()

                // Проверяем, есть ли #access_token в ссылке
                val token = if (fullUri.contains("#access_token=")) {
                    fullUri.substringAfter("#access_token=").substringBefore("&")
                } else {
                    uri.getQueryParameter("token") ?: ""
                }

                Log.d("DeepLink", "Extracted token and email: $token")

                if (token.isNotBlank()) {
                    Log.d("DeepLink", "Navigating with token and email: $token")
                    navController.navigate(
                        R.id.updatePasswordFragment,
                        Bundle().apply { putString(TOKEN, token) })

                }
            }
        }
    }
    companion object {
        private const val TOKEN = "token"
    }
    }
















