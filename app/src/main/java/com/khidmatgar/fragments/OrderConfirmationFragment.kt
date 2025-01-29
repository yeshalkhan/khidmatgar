package com.khidmatgar.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khidmatgar.activities.MainActivity
import com.khidmatgar.databinding.FragmentOrderConfirmationBinding

class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.homePageButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        binding.viewScheduleButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("openFragment", "BookingsFragment")
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}