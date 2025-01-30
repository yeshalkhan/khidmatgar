package com.khidmatgar.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.khidmatgar.R
import com.khidmatgar.databinding.ActivityMainBinding
import com.khidmatgar.server_integration.RetrofitInstance
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Store the email when the app is opened
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email

        if (email != null) {
            // Save email to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("userEmail", email)
            editor.apply()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navView.setBackgroundColor(getResources().getColor(R.color.white));

        val profile: CircleImageView = findViewById(R.id.profilePictureLink)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_bookings, R.id.nav_saved, R.id.nav_logout),
            drawerLayout
        )

        // Handle back button presses using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    finish() // Exits the activity
                }
            }
        })

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    signOutUser()
                    drawerLayout.closeDrawers()
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        // Change the color of the toolbar navigation (toggle) button
        binding.appBarMain.toolbar.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            if (destination.id == R.id.nav_home) {
                findViewById<View>(R.id.logo).visibility = View.VISIBLE
                supportActionBar!!.setDisplayShowTitleEnabled(false)
            } else {
                findViewById<View>(R.id.logo).visibility = View.GONE
                supportActionBar!!.setDisplayShowTitleEnabled(true)
            }
        }

        val headerView = navView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.userNameTextView)
        val userEmailTextView: TextView = headerView.findViewById(R.id.userEmailTextView)

        // Check if the activity was started with a specific fragment request
        val openFragment = intent.getStringExtra("openFragment")
        if (openFragment == "BookingsFragment") {
            navController.navigate(R.id.nav_bookings)
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getUserByEmail(email ?: "")
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        val name = response.body()?.firstName + " " + response.body()?.lastName
                        userNameTextView.text = name
                        userEmailTextView.text = email
                        profile.setOnClickListener {
                            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Log.e("MainActivity", "User not found")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching user", e)
            }
        }
    }

    private fun signOutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val editor = sharedPreferences.edit()
        editor.clear() // Remove all saved data
        editor.apply()

        val intent = Intent(this, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finish the current activity to prevent going back to it
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear email when the app is closed
        clearUserEmail()
    }

    private fun clearUserEmail() {
        val editor = sharedPreferences.edit()
        editor.remove("userEmail")
        editor.apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
