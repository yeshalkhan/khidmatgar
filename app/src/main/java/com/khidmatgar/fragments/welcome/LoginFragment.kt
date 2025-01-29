package com.khidmatgar.fragments.welcome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.khidmatgar.activities.MainActivity
import com.khidmatgar.R
import com.khidmatgar.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        binding.signupLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val remember = binding.rememberMe.isChecked

            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this@LoginFragment.context,"Please enter all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        if (remember)
                        {
                            saveLoginStatus(true)
                        }
                        // Navigate to another activity, e.g., MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Login failed
                        Toast.makeText(requireContext(), "Incorrect email or password ", Toast.LENGTH_LONG).show()
                    }
                }
        }
        return root
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
