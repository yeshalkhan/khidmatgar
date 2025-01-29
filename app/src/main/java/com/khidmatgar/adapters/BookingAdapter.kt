package com.khidmatgar.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khidmatgar.R
import com.khidmatgar.activities.MainActivity
import com.khidmatgar.activities.ServiceProviderActivity
import com.khidmatgar.models.Booking
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.databinding.ItemBookingBinding
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class BookingAdapter(
    private val bookings: MutableList<Pair<Booking, ServiceProvider>>,
    private val context: Context
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemBookingBinding.bind(itemView)
        private val viewHolderScope = CoroutineScope(Dispatchers.Main)

        fun bind(booking: Booking, serviceProvider: ServiceProvider) {
            binding.serviceNameTextView.text = serviceProvider.serviceName
            binding.serviceProviderNameTextView.text = serviceProvider.name
            binding.ratingTextView.text = serviceProvider.rating?.toString() ?: "N/A"
            binding.rateTextView.text = "$${serviceProvider.rate}/h"
            val imageUrl = RetrofitInstance.BASE_URL + serviceProvider.image
            Glide.with(itemView.context).load(imageUrl).into(binding.serviceProviderImage)

            val deliveryAt = booking.deliveryAt
            val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, EEEE hh:mm a", Locale.getDefault())
            val dateTime = dateTimeFormat.parse(deliveryAt)

            val dateFormat = SimpleDateFormat("dd MMM yyyy, EEEE", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val formattedDate = dateFormat.format(dateTime!!)
            val formattedTime = timeFormat.format(dateTime)

            binding.dateTextView.text = formattedDate
            binding.timeTextView.text = formattedTime

            binding.serviceProviderImage.setOnClickListener {
                val intent = Intent(itemView.context, ServiceProviderActivity::class.java)
                intent.putExtra("SERVICE_PROVIDER_ID", serviceProvider.id)
                itemView.context.startActivity(intent)
            }

            binding.cancelBookingButton.setOnClickListener {
                showDialog(booking)
            }

            binding.statusTextView.text = booking.status ?: "Active"

            // Update statusTextView based on booking status
            when (booking.status) {
                "Active" -> {
                    binding.statusTextView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.light_green))
                    binding.statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_green))
                    binding.cancelBookingButton.visibility = View.VISIBLE

                }
                "Done" -> {
                    binding.statusTextView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.sky_blue))
                    binding.statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
                }
                "Cancelled" -> {
                    binding.statusTextView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.light_red))
                    binding.statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_red))
                }
                else -> {
                    // Handle default case or unknown status
                    binding.statusTextView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.black))
                    binding.statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }
            }
        }

        private fun showDialog(booking: Booking) {
            val customDialog = Dialog(context)
            customDialog.setContentView(R.layout.dialog_order_cancellation)
            customDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val cancelBookingButton: Button = customDialog.findViewById(R.id.confirmCancellation)
            val whiteColor = ContextCompat.getColor(customDialog.context, R.color.white)
            cancelBookingButton.backgroundTintList = ColorStateList.valueOf(whiteColor)

            val closeDialogButton : Button = customDialog.findViewById(R.id.closeDialog)
            cancelBookingButton.setOnClickListener {
                cancelBooking(booking)
                customDialog.dismiss()
            }
            closeDialogButton.setOnClickListener {
                customDialog.dismiss()
            }
            customDialog.show()
        }

        private fun cancelBooking(booking: Booking)
        {
            viewHolderScope.launch(Dispatchers.IO) {
                try{
                    booking.status = "Cancelled"
                    val response = RetrofitInstance.api.updateBooking(booking.id!!, booking)
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show()
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                notifyItemChanged(position)
                            }
                        }
                    }
                    else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch(ex: Exception)
                {
                    Log.e("Error connecting to server", ex.message.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val (booking, serviceProvider) = bookings[position]
        holder.bind(booking, serviceProvider)
    }

    override fun getItemCount(): Int = bookings.size
}
