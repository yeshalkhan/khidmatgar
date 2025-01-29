package com.khidmatgar.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import com.khidmatgar.R
import com.khidmatgar.databinding.FragmentBookingProcessBinding
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.models.Booking
import com.khidmatgar.repositories.UserRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.SharedPreferences
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

class BookingProcessFragment : Fragment() {

    private var _binding: FragmentBookingProcessBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var serviceProvider: ServiceProvider? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingProcessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Retrieve the serviceProvider object from arguments
        arguments?.let {
            serviceProvider = it.getParcelable("serviceProvider")
        }

        val price = "$${serviceProvider?.rate}/h"
        binding.totalPrice.text = price

        setupCalendarView()
        setupSeekBar()
        setupTimePicker()

        binding.confirmButton.setOnClickListener {
            createBooking()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Navigate back to the previous fragment or activity
            requireActivity().finish()
        }
    }

    private fun setupCalendarView() {
        val calendarView = binding.calendarView

        val dateTime: Date = Calendar.getInstance().time
        val dateTimeAsLong: Long = dateTime.time
        calendarView.minDate = dateTimeAsLong

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val actualMonth = month + 1
            selectedDate = "$year-${String.format("%02d", actualMonth)}-${String.format("%02d", dayOfMonth)}"
        }
    }

    private fun setupSeekBar() {
        val seekBar = binding.seekBar
        val textView = binding.noOfHours

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val noOfHours = "Hours: ${progress + 1}"
                textView.text = noOfHours
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupTimePicker() {
        val timePicker = binding.timeInput
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        }
    }

    private fun createBooking() {
        val location = binding.locationInput.text.toString()

        if(location == null || location == "") {
            Toast.makeText(requireContext(), "Please select location", Toast.LENGTH_SHORT).show()
            return
        }
        // Format the current date and time for createdAt
        val createdAtFormatter = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
        val createdAt = createdAtFormatter.format(Date())

        // Check if both date and time are selected
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(requireContext(), "Please select both date and time", Toast.LENGTH_SHORT).show()
            return
        }

        // Combine selectedDate and selectedTime to form deliveryAt
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDateString = "$selectedDate ${selectedTime?.let { it + ":00" }}" // Add seconds to match format
        val formattedDate: Date

        try {
            formattedDate = dateTimeFormatter.parse(formattedDateString) ?: Date()
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Date parsing error.", Toast.LENGTH_SHORT).show()
            return
        }

        // Format deliveryAt to match the server's expected format
        val deliveryAtFormatter = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
        val deliveryAtFormatted = deliveryAtFormatter.format(formattedDate)

        // Get the number of hours from the SeekBar
        val noOfHours = binding.seekBar.progress + 1

        // Calculate the price based on the service provider's rate and number of hours
        val rate = (serviceProvider?.rate ?: 0.0).toDouble()
        val totalPrice = rate * noOfHours

        // Initialize the Booking object
        val booking = Booking(
            serviceProviderId = serviceProvider?.id ?: -1,
            customerEmail = sharedPreferences.getString("userEmail", null)!!,
            serviceName = serviceProvider?.serviceName ?: "",
            price = totalPrice,
            noOfHours = noOfHours,
            createdAt = createdAt,
            deliveryAt = deliveryAtFormatted,
            location = location,
            status = "Active"
        )

        // Create a Bundle and pass the Booking object and additional info
        val bundle = Bundle().apply {
            putParcelable("booking", booking)
            putParcelable("serviceProvider", serviceProvider)
        }

        // Navigate to OrderReviewFragment with the bundle
        findNavController().navigate(R.id.action_BookingProcessFragment_to_OrderReviewFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
