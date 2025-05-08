package com.example.vetclinic

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vetclinic.presentation.providers.ViewModelFactory
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
            Log.d(TAG, "Received URI: $uri")

            if (uri.host == "reset-password") {
                val fullUri = uri.toString()

                val token = fullUri.substringAfter("#access_token=").substringBefore("&")

                Log.d(TAG, "Extracted token: $token")

                if (token.isNotBlank()) {
                    Log.d(TAG, "Navigating with token: $token")

                    navController.navigate(
                        R.id.updatePasswordFragment,
                        Bundle().apply { putString(TOKEN, token) })
                }
            }
        }
    }
    companion object {
        private const val TOKEN = "token"
        private const val TAG = "MainActivity"
    }
    }
















