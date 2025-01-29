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
import com.khidmatgar.databinding.FragmentServiceProvidersBinding
import com.khidmatgar.models.ServiceProvider

class ServiceProvidersFragment : Fragment() {

    private var _binding: FragmentServiceProvidersBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServiceProvidersBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", null)!!

        // Retrieve and cast Parcelable array to Array<ServiceProvider>
        val serviceProviderArray = arguments?.getParcelableArray("serviceProviders")?.filterIsInstance<ServiceProvider>()

        if (serviceProviderArray != null) {
            val serviceProviderList = serviceProviderArray.toMutableList()
            binding.serviceProvidersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.serviceProvidersRecyclerView.adapter = ServiceProviderAdapter(serviceProviderList, 1, requireContext(), userEmail)
        }

        return binding.root
    }
}