package com.example.bookk.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bookk.R
import com.example.bookk.databinding.ActivityDashboardBinding
import com.example.bookk.ui.fragment.AddFragment
import com.example.bookk.ui.fragment.HomeFragment
import com.example.bookk.ui.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display

        // Initialize ViewBinding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up BottomNavigationView
        setupBottomNavigationView()

        // Set default fragment (Home)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.add -> {
                    replaceFragment(AddFragment())
                    true
                }
                R.id.person -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // ✅ Set default selected item to "Home"
        bottomNavigationView.selectedItemId = R.id.home
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frameLayout)

        // Avoid reloading the same fragment
        if (currentFragment == null || currentFragment::class.java != fragment::class.java) {
            fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null) // This allows for fragment navigation with back press
                .commit()
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

