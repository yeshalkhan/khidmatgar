package com.khidmatgar.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khidmatgar.R
import com.khidmatgar.adapters.ServiceProviderAdapter
import com.khidmatgar.databinding.FragmentHomeBinding
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle the search query text change
                return true
            }
        })

        // Fetch data using lifecycle-aware coroutine scope
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val userEmail = sharedPreferences.getString("userEmail", null)!!
            try {
                val response = RetrofitInstance.api.getAllServiceProviders()
                if (response.isSuccessful) {
                    // Extract the list from the response body
                    val serviceProviders: MutableList<ServiceProvider>? = response.body()

                    if (serviceProviders != null) {
                        val sortedProviders = serviceProviders.sortedByDescending { it.rating }

                        // Select the top 5 rated providers
                        val topProviders = if (sortedProviders.size > 5) {
                            sortedProviders.take(5)
                        } else {
                            sortedProviders
                        }
                        // Switch to the Main thread for UI updates
                        withContext(Dispatchers.Main) {
                            binding.recommendationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                            binding.recommendationsRecyclerView.adapter = ServiceProviderAdapter(topProviders.toMutableList(), 1, requireContext(), userEmail)
                            setupServiceProvidersClickListeners(sortedProviders)
                            setupCategoryClickListeners(sortedProviders)
                        }
                    }
                } else {
                    Log.e("Error fetching service providers", "${response.code()} ${response.message()}")
                }
            } catch (ex: Exception) {
                Log.e("Error connecting to server", ex.message.toString())
            }
        }

        return binding.root
    }

    private fun performSearch(seachTerm: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.searchServiceProvider(seachTerm)
                if (response.isSuccessful)
                {
                    val serviceProviders: MutableList<ServiceProvider>? = response.body()
                    withContext(Dispatchers.Main)
                    {
                        val bundle = bundleOf("serviceProviders" to serviceProviders?.toTypedArray())
                        findNavController().navigate(R.id.action_homeFragment_to_searchFragment, bundle)
                    }
                }
                else
                    Log.e("Error searching", "${response.code()} ${response.message()}")
            } catch (ex: Exception)
            {
                Log.e("Error connecting to server", ex.message.toString())
            }
        }
    }

    private fun setupServiceProvidersClickListeners(serviceProviders: List<ServiceProvider>){
        binding.seeAllRecommendedLink.setOnClickListener{
            val bundle = bundleOf("serviceProviders" to serviceProviders.toTypedArray())
            findNavController().navigate(R.id.action_homeFragment_to_serviceProvidersFragment, bundle)
        }

        binding.btnGetStarted.setOnClickListener {
            val bundle = bundleOf("serviceProviders" to serviceProviders.toTypedArray())
            findNavController().navigate(R.id.action_homeFragment_to_serviceProvidersFragment, bundle)
        }
    }

    private fun setupCategoryClickListeners(serviceProviders: List<ServiceProvider>) {
        binding.cleaningCategory.setOnClickListener {
            val cleaningProviders = serviceProviders.filter { it.serviceName == "Cleaning" }
            navigateToCategory(
                categoryCoverImage = R.drawable.cover_cleaning_page,
                categoryHeading = getString(R.string.cleaning_category),
                categoryAbout = getString(R.string.cleaning_page_description),
                serviceProviderList = cleaningProviders
            )
        }

        binding.cookingCategory.setOnClickListener {
            val cookingProviders = serviceProviders.filter { it.serviceName == "Cooking" }
            navigateToCategory(
                categoryCoverImage = R.drawable.cover_cooking_page,
                categoryHeading = getString(R.string.cooking_category),
                categoryAbout = getString(R.string.cooking_page_description),
                serviceProviderList = cookingProviders
            )
        }

        binding.laundryCategory.setOnClickListener {
            val laundryProviders = serviceProviders.filter { it.serviceName == "Laundry" }
            navigateToCategory(
                categoryCoverImage = R.drawable.cover_laundry_page,
                categoryHeading = getString(R.string.laundry_category),
                categoryAbout = getString(R.string.laundry_page_description),
                serviceProviderList = laundryProviders
            )
        }

        binding.paintingCategory.setOnClickListener {
            val paintingProviders = serviceProviders.filter { it.serviceName == "Painting" }
            navigateToCategory(
                categoryCoverImage = R.drawable.cover_painting_page,
                categoryHeading = getString(R.string.painting_category),
                categoryAbout = getString(R.string.painting_page_description),
                serviceProviderList = paintingProviders
            )
        }
    }

    private fun navigateToCategory(
        categoryCoverImage: Int,
        categoryHeading: String,
        categoryAbout: String,
        serviceProviderList: List<ServiceProvider>
    ) {
        val bundle = bundleOf(
            "categoryCoverImage" to categoryCoverImage,
            "categoryHeading" to categoryHeading,
            "categoryAbout" to categoryAbout,
            "serviceProviderList" to serviceProviderList.toTypedArray()
        )
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
