package com.example.vetclinic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vetclinic.domain.usecases.HandleDeepLinkUseCase
import com.example.vetclinic.presentation.screens.updatePasswordScreen.PasswordUpdateMode
import com.example.vetclinic.presentation.screens.updatePasswordScreen.UpdatePasswordFragment
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {


    private val component by lazy {
        (application as VetClinicApplication).component
    }

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

        if (savedInstanceState == null) {
            handleDeepLink(intent)
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }


    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            Timber.tag(TAG).d("Received URI: $uri")

            lifecycleScope.launch {
                val result = deepLinkUseCase.handleDeepLink(uri)
                if (result.isSuccess) {
                    val updatePasswordFragment = UpdatePasswordFragment.newInstance(
                        PasswordUpdateMode.FROM_DEEPLINK
                    )

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, updatePasswordFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Timber.tag(TAG)
                        .d("Error processing deep link: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
















