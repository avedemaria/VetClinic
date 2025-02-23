package com.example.vetclinic.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vetclinic.R
//
//class MainScreenActivity : AppCompatActivity() {
//
//    private val component by lazy {
//        (application as VetClinicApplication).component
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main_screen)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        component.inject(this)
//
//
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.selection_nav_host_fragment)
//                    as NavHostFragment
//        val navController = navHostFragment.navController
//        val clinicInfoContainer = findViewById<View>(R.id.clinic_info_container)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            clinicInfoContainer.visibility =
//                if (destination.id == R.id.selectionFragment) View.VISIBLE else View.GONE
//        }
//    }
//}


