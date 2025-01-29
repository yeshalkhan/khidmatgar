package com.khidmatgar.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.khidmatgar.R
import com.khidmatgar.databinding.ActivityStartupBinding

class StartupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_welcome) as? NavHostFragment

        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (firebaseAuth.currentUser != null && isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            sharedPreferences.edit().clear().apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_welcome)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
