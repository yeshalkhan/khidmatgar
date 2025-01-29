package com.khidmatgar.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khidmatgar.R
import com.khidmatgar.adapters.ServiceProviderAdapter
import com.khidmatgar.databinding.FragmentCategoryBinding
import com.khidmatgar.models.ServiceProvider

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", null)!!

        // Hide the app bar for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Retrieve the arguments passed from HomeFragment
        val coverImageResId = arguments?.getInt("categoryCoverImage")
        val heading = arguments?.getString("categoryHeading")
        val aboutCategory = arguments?.getString("categoryAbout")

        // Set data to views
        if (coverImageResId != null) {
            binding.categoryCoverImage.setBackgroundResource(coverImageResId)
        }
        binding.categoryHeading.text = heading
        binding.categoryAbout.text = aboutCategory

        // Retrieve and cast Parcelable array to Array<ServiceProvider>
        val serviceProviderArray = arguments?.getParcelableArray("serviceProviderList")?.filterIsInstance<ServiceProvider>()

        if (serviceProviderArray != null) {
            val serviceProviderList = serviceProviderArray.toMutableList()
            binding.serviceProvidersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.serviceProvidersRecyclerView.adapter = ServiceProviderAdapter(serviceProviderList, 2, requireContext(), userEmail)
        }

        binding.backButton.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // Restore the app bar when leaving this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }
}
