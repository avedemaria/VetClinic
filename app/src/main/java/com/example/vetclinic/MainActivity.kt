package com.example.vetclinic

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vetclinic.domain.usecases.HandleDeepLinkUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val component by lazy {
        (application as VetClinicApplication).component
    }

    private lateinit var navController: NavController

    @Inject
    lateinit var deepLinkUseCase: HandleDeepLinkUseCase


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

            lifecycleScope.launch {
                val result = deepLinkUseCase.handleDeepLink(uri)
                if (result.isSuccess) {
                    navController.navigate(
                        R.id.updatePasswordFragment,
                    )
                } else {
                    Log.d(TAG, "Error processing deep link: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
















