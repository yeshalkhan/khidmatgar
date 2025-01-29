package com.khidmatgar.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.khidmatgar.adapters.ServiceProviderAdapter
import com.khidmatgar.databinding.FragmentSearchBinding
import com.khidmatgar.models.ServiceProvider

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", null)!!

        // Retrieve and cast Parcelable array to Array<ServiceProvider>
        val serviceProviderArray = arguments?.getParcelableArray("serviceProviders")?.filterIsInstance<ServiceProvider>()

        if (serviceProviderArray != null) {
            val serviceProviderList = serviceProviderArray.toMutableList()
            binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.searchRecyclerView.adapter = ServiceProviderAdapter(serviceProviderList, 1, requireContext(), userEmail)
        }

        return binding.root
    }

}