package com.khidmatgar.fragments

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.khidmatgar.R
import com.khidmatgar.activities.MainActivity
import com.khidmatgar.activities.ServiceProviderActivity
import com.khidmatgar.databinding.FragmentOrderReviewBinding
import com.khidmatgar.models.Booking
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.logging.Logger.global

class OrderReviewFragment : Fragment() {

    private var _binding: FragmentOrderReviewBinding? = null
    private val binding get() = _binding!!
    private var selectedPaymentOption: String? = null
    private var serviceProviderId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderReviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Retrieve arguments and set up the UI
        setupUI()

        // Handle button clicks
        setupButtonListeners()

        return root
    }

    private fun setupUI() {
        arguments?.let { bundle ->
            val booking = bundle.getParcelable<Booking>("booking")
            val serviceProvider = bundle.getParcelable<ServiceProvider>("serviceProvider")
            val serviceProviderImage = serviceProvider?.image
            val serviceProviderName = serviceProvider?.name
            val serviceCharges = serviceProvider?.rate
            val serviceName = serviceProvider?.serviceName
            val discount = serviceProvider?.discount

            // Set serviceProviderId to the class-level variable
            serviceProviderId = serviceProvider?.id

            // Load image using Glide
            val imageUrl = RetrofitInstance.BASE_URL + serviceProviderImage
            context?.let { Glide.with(it).load(imageUrl).into(binding.serviceProviderImage) }

            // Set text views
            val serviceProviderNameValue = "by $serviceProviderName"
            binding.serviceProviderNameTextView.text = serviceProviderNameValue
            binding.serviceNameTextView.text = serviceName
            binding.addressTextView.text = booking?.location

            // Format date and time for display
            val displayDateFormat = SimpleDateFormat("dd MMM yyyy, EEEE", Locale.getDefault())
            val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val deliveryDateStr = booking?.deliveryAt
            val serverDateFormat = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
            val deliveryDate = deliveryDateStr?.let {
                try {
                    serverDateFormat.parse(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            binding.dateTextView.text = deliveryDate?.let { displayDateFormat.format(it) }
            binding.timeTextView.text = deliveryDate?.let { displayTimeFormat.format(it) }

            // Set additional UI elements
            binding.noOfHoursTextView.text = booking?.noOfHours.toString()
            val charges = "$${serviceCharges}/h"
            binding.serviceChargesTextView.text = charges
            val price = "$${booking?.price}"
            if (discount == null)
                binding.totalChargesTextView.text = price
            else {
                binding.subtotalLayout.visibility = View.VISIBLE
                binding.discountLayout.visibility = View.VISIBLE
                binding.subtotalTextView.text = price
                binding.discountTextView.text = "${discount}%"
                booking?.price = booking?.price?.minus(discount.toDouble() / 100 * (booking.price!!))
                binding.totalChargesTextView.text = "$${booking?.price}"
            }
        }
    }

    private fun setupButtonListeners() {
        binding.viewServiceProvider.setOnClickListener {
            val intent = Intent(context, ServiceProviderActivity::class.java)
            intent.putExtra("SERVICE_PROVIDER_ID", serviceProviderId)
            startActivity(intent)
        }

        binding.cashButton.setOnClickListener {
            selectedPaymentOption = "COD"
            binding.cashButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.light_peach)
            binding.onlinePaymentButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
        }

        binding.onlinePaymentButton.setOnClickListener {
            selectedPaymentOption = "Online Payment"
            binding.onlinePaymentButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.light_peach)
            binding.cashButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
        }

        binding.confirmOrderButton.setOnClickListener {
            if (selectedPaymentOption == null) {
                Toast.makeText(context, "Please select a payment option", Toast.LENGTH_SHORT).show()
            } else {
                confirmBooking()
            }
        }
    }

    private fun confirmBooking() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val booking = arguments?.getParcelable<Booking>("booking")
                booking?.paymentOption = selectedPaymentOption

                if (booking != null) {
                    val response = RetrofitInstance.api.createBooking(booking)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            findNavController().navigate(R.id.action_OrderReviewFragment_to_OrderConfirmationFragment)
                        } else {
                            Toast.makeText(context, "Failed to confirm booking. Please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Booking information is incomplete", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error connecting to server", ex.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
