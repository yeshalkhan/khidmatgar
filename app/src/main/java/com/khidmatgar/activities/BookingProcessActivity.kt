package com.khidmatgar.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.khidmatgar.R
import com.khidmatgar.databinding.ActivityBookingProcessBinding
import com.khidmatgar.models.ServiceProvider

class BookingProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingProcessBinding
    private var serviceProvider: ServiceProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the serviceProvider object from intent
        serviceProvider = intent.getParcelableExtra("SERVICE_PROVIDER")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_booking_process) as? NavHostFragment

        val navController = navHostFragment?.navController ?: throw IllegalStateException("NavHostFragment not found")

        // Pass the serviceProvider object to the BookingProcessFragment
        val bundle = Bundle().apply {
            putParcelable("serviceProvider", serviceProvider)
        }

        // Navigate to BookingProcessFragment with the bundle
        navController.navigate(R.id.BookingProcessFragment, bundle)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_booking_process) as? NavHostFragment
        val navController = navHostFragment?.navController ?: return super.onSupportNavigateUp()
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
