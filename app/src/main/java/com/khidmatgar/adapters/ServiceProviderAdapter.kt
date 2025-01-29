package com.khidmatgar.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.khidmatgar.R
import com.khidmatgar.activities.ServiceProviderActivity
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.repositories.BookmarkRepository
import com.khidmatgar.repositories.UserRepository
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceProviderAdapter(
    private val serviceProviders: MutableList<ServiceProvider>,
    private val viewType: Int,
    private val context: Context,
    private val userEmail: String?
) : RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder>() {

    private val VIEW_TYPE_LAYOUT1 = 1  // for home page
    private val VIEW_TYPE_LAYOUT2 = 2  // for category page
    private val VIEW_TYPE_LAYOUT3 = 3  // for bookmarks page
    private val bookmarkRepository = BookmarkRepository(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProviderViewHolder {
        val layout = when (this.viewType) {
            VIEW_TYPE_LAYOUT1 -> R.layout.item_recommended
            VIEW_TYPE_LAYOUT2 -> R.layout.item_service_provider
            VIEW_TYPE_LAYOUT3 -> R.layout.item_service_provider
            else -> throw IllegalArgumentException("Invalid view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ServiceProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceProviderViewHolder, position: Int) {
        val serviceProvider = serviceProviders[position]
        holder.bind(serviceProvider)
    }

    override fun getItemCount(): Int = serviceProviders.size

    inner class ServiceProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView? = itemView.findViewById(R.id.nameTextView)
        private val imageView: ShapeableImageView? = itemView.findViewById(R.id.serviceProviderImage)
        private val ratingTextView: TextView? = itemView.findViewById(R.id.ratingTextView)
        private val priceTextView: TextView? = itemView.findViewById(R.id.priceTextView)
        private val discountTextView: TextView? = itemView.findViewById(R.id.discountTextView)
        private val serviceNameTextView: TextView? = itemView.findViewById(R.id.serviceNameTextView)
        private val bookmarkButton: ImageView? = itemView.findViewById(R.id.bookmarkButton)
        private val viewServiceProviderButton: View? = itemView.findViewById(R.id.viewServiceProvider)

        private val viewHolderScope = CoroutineScope(Dispatchers.Main)

        fun bind(serviceProvider: ServiceProvider) {
            serviceNameTextView?.text = serviceProvider.serviceName
            nameTextView?.text = serviceProvider.name
            ratingTextView?.text = serviceProvider.rating?.takeIf { it != -1.0 }?.toString() ?: "N/A"
            priceTextView?.text = "$${serviceProvider.rate}/h"

            val imageUrl = RetrofitInstance.BASE_URL + serviceProvider.image
            Glide.with(itemView.context).load(imageUrl).into(imageView ?: return)

            discountTextView?.visibility = if (serviceProvider.discount?.takeIf { it != -1 } != null) {
                discountTextView?.text = "${serviceProvider.discount}% Off"
                View.VISIBLE
            } else {
                View.GONE
            }

            handleBookmarkButton(serviceProvider.id!!)

            // Handle view service provider button click
            viewServiceProviderButton?.setOnClickListener {
                val serviceProviderId = serviceProvider.id
                val intent = Intent(itemView.context, ServiceProviderActivity::class.java)
                intent.putExtra("SERVICE_PROVIDER_ID", serviceProviderId)
                itemView.context.startActivity(intent)
            }
        }

        private fun handleBookmarkButton(serviceProviderId: Int)
        {
            // Check bookmark status initially to set the button icon
            viewHolderScope.launch {
                val email = userEmail!!
                val isBookmarked = bookmarkRepository.isBookmarkedLocally(email, serviceProviderId).first()
                updateBookmarkButton(isBookmarked)

                // Handle bookmark button click
                bookmarkButton?.setOnClickListener {
                    viewHolderScope.launch(Dispatchers.IO) {
                        val currentIsBookmarked = bookmarkRepository.isBookmarkedLocally(email, serviceProviderId).first()
                        if (currentIsBookmarked) {
                            // Unmark
                            bookmarkRepository.deleteBookmarkLocally(email, serviceProviderId)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                                updateBookmarkButton(false)

                                // Refresh the layout if we're on Bookmarks page
                                if (viewType == 3) {
                                    val position = adapterPosition
                                    if (position != RecyclerView.NO_POSITION) {
                                        serviceProviders.removeAt(position)
                                        notifyItemRemoved(position)
                                    }
                                }
                            }
                        } else {
                            // Bookmark
                            bookmarkRepository.saveBookmarkLocally(email, serviceProviderId)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show()
                                updateBookmarkButton(true)
                            }
                        }
                    }
                }
            }
        }

        private fun updateBookmarkButton(isBookmarked: Boolean) {
            bookmarkButton?.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark
            )
        }
    }
}
