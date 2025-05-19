package com.example.easyfood.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.easyfood.R
import com.example.easyfood.UserSessionManager
import com.example.easyfood.databinding.ActivityMainBinding
import com.example.easyfood.databinding.FragmentHomeBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.viewModel.HomeViewModel
import com.example.easyfood.viewModel.HomeViewModelFactory
import com.example.easyfood.viewModel.SharedUserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel: HomeViewModel by lazy {
        val mealDatabase = MealDatabase.getInstance(this)
        val homeViewModelProviderFactory = HomeViewModelFactory(mealDatabase)
        ViewModelProvider(this, homeViewModelProviderFactory).get(HomeViewModel::class.java)
    }

    val userViewModel: SharedUserViewModel by lazy {
        ViewModelProvider(this)[SharedUserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.btm_nav)
        val navController = Navigation.findNavController(this, R.id.host_fragment)
        NavigationUI.setupWithNavController(bottomNavigation, navController)

        binding.btmNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.favoritesFragment -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }
                R.id.categoriesFragment -> {
                    navController.navigate(R.id.categoriesFragment)
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

        // Restore session and put user in SharedUserViewModel
        val restoredUserId = UserSessionManager.getLoggedInUserId(this)
        Log.d("MainActivity", "Restored session user ID: $restoredUserId")

        if (restoredUserId != -1) {
            lifecycleScope.launch {
                val user = MealDatabase.getInstance(this@MainActivity)
                    .userDao()
                    .getUserById(restoredUserId)

                user?.let {
                    userViewModel.setUser(it)
                }
            }
        }
    }

}