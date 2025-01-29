package com.khidmatgar.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.khidmatgar.R
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceProviderActivity : AppCompatActivity() {

    private var serviceProviderId: Int? = null
    private lateinit var serviceProvider: ServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider)

        val toolbar: Toolbar = findViewById(R.id.toolbarServiceProviderPage)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        serviceProviderId = intent.getIntExtra("SERVICE_PROVIDER_ID", -1)

        serviceProviderId?.let { id ->
            loadServiceProvider(id)
        }
    }

    private fun loadServiceProvider(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getServiceProviderById(id)
                if (response.isSuccessful) {
                    serviceProvider = response.body()!!
                    if (serviceProvider != null) {
                        withContext(Dispatchers.Main) {
                            updateUI()
                            setupButtonListeners()
                        }
                    }
                } else {
                    Log.e("Error fetching service provider", "${response.code()} ${response.message()}")
                }
            } catch (ex: Exception) {
                Log.e("Error connecting to server", ex.message.toString())
            }
        }
    }

    private fun updateUI() {
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val imageView = findViewById<ShapeableImageView>(R.id.serviceProviderImage)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val ratingTextView = findViewById<TextView>(R.id.ratingTextView)
        val priceTextView = findViewById<TextView>(R.id.priceTextView)
        val discountTextView = findViewById<TextView>(R.id.discountTextView)
        val serviceNameTextView = findViewById<TextView>(R.id.serviceNameTextView)

        serviceNameTextView.text = serviceProvider.serviceName
        nameTextView.text = serviceProvider.name
        ratingTextView.text = serviceProvider.rating?.takeIf { it != -1.0 }?.toString() ?: "N/A"
        priceTextView.text = "$${serviceProvider.rate}/h"

        val imageUrl = RetrofitInstance.BASE_URL + serviceProvider.image
        Glide.with(this).load(imageUrl).into(imageView)

        discountTextView.visibility = if (serviceProvider.discount?.takeIf { it != -1 } != null) {
            discountTextView.text = "${serviceProvider.discount}% Off"
            View.VISIBLE
        } else {
            View.GONE
        }

        addressTextView.text = serviceProvider.address
        descriptionTextView.text = serviceProvider.description
    }

    private fun setupButtonListeners() {
        val bookButton = findViewById<Button>(R.id.bookButton)

        bookButton.setOnClickListener {
            val intent = Intent(this, BookingProcessActivity::class.java)
            intent.putExtra("SERVICE_PROVIDER", serviceProvider)
            startActivity(intent)
        }

        val contactButton = findViewById<Button>(R.id.contactButton)

        contactButton.setOnClickListener {
            val phoneNumber = serviceProvider.phoneNumber
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(phoneIntent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
