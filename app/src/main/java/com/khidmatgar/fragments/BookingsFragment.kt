package com.khidmatgar.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khidmatgar.adapters.BookingAdapter
import com.khidmatgar.databinding.FragmentBookingsBinding
import com.khidmatgar.models.Booking
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.repositories.UserRepository
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    private val apiService = RetrofitInstance.api
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchBookings()
    }

    private fun fetchBookings() {
        CoroutineScope(Dispatchers.IO).launch {
            val userEmail = sharedPreferences.getString("userEmail", null)!!
            val response = apiService.getBookingsByUserEmail(userEmail)
            if (response.isSuccessful) {
                val bookings = response.body() ?: emptyList()
                val bookingList = mutableListOf<Pair<Booking, ServiceProvider>>()
                bookings.forEach { booking ->
                    val serviceProviderResponse = apiService.getServiceProviderById(booking.serviceProviderId)
                    if (serviceProviderResponse.isSuccessful) {
                        val serviceProvider = serviceProviderResponse.body()
                        if (serviceProvider != null) {
                            bookingList.add(Pair(booking, serviceProvider))
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    val adapter = BookingAdapter(bookingList, requireContext())
                    binding.bookingRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.bookingRecyclerView.adapter = adapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
