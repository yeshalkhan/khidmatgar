package com.khidmatgar.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.khidmatgar.R
import com.khidmatgar.adapters.ServiceProviderAdapter
import com.khidmatgar.databinding.FragmentBookmarksBinding
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.repositories.BookmarkRepository
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bookmarkRepository: BookmarkRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookmarkRepository = BookmarkRepository(requireContext())
        binding.bookmarksRecyclerView.layoutManager = LinearLayoutManager(context)
        fetchBookmarks()
    }

    private fun fetchBookmarks() {
        val userEmail = sharedPreferences.getString("userEmail", null)!!

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Retrieve bookmarked service provider IDs
                val bookmarkedIds = bookmarkRepository.getBookmarkedServiceProviderIds(userEmail).first()

                // Fetch service provider details for each bookmarked ID
                val serviceProviders = mutableListOf<ServiceProvider>()

                for (id in bookmarkedIds) {
                    loadServiceProvider(id.toInt())?.let {
                        serviceProviders.add(it)
                    }
                }

                withContext(Dispatchers.Main) {
                    // Set up RecyclerView adapter with the list of service providers
                    binding.bookmarksRecyclerView.adapter = ServiceProviderAdapter(serviceProviders, 3, requireContext(), userEmail)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error fetching bookmarks", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private suspend fun loadServiceProvider(id: Int): ServiceProvider? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getServiceProviderById(id)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("Error fetching service provider", "${response.code()} ${response.message()}")
                    null
                }
            } catch (ex: Exception) {
                Log.e("Error connecting to server", ex.message.toString())
                null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
