package com.khidmatgar.fragments.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.khidmatgar.activities.MainActivity
import com.khidmatgar.R
import com.khidmatgar.databinding.FragmentSignupBinding
import com.khidmatgar.models.User
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val auth = FirebaseAuth.getInstance()

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.signupButton.setOnClickListener {

            val firstName = binding.firstNameField.text.toString()
            val lastName = binding.lastNameField.text.toString()
            val phone = binding.phoneNumberField.text.toString()
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val confirmPassword = binding.confirmPasswordField.text.toString()


            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener;
            }

            if (checkIfEmailExists(email)) {
                Toast.makeText(requireContext(), "This email already exists", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(password != confirmPassword)
            {
                Toast.makeText(requireContext(), "Password and Confirm Password do not match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length < 6)
            {
                Toast.makeText(requireContext(), "Password must be of at least 6 characters", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        val user = User(
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            location = null,
                            phoneNo = phone
                        )

                        GlobalScope.launch {
                            try {
                                val response = RetrofitInstance.api.addUser(user)
                                if (response.isSuccessful) {
                                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                                } else {
                                    Log.e("ProfileActivity", "Error creating user: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ProfileActivity", "Connection Error 2", e)
                            }
                        }
                    }
                    else
                        Toast.makeText(requireContext(),task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
        }

        return root
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkIfEmailExists(email: String) : Boolean {
        var flag : Boolean = false
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.api.getUserByEmail(email)
                if (response.isSuccessful) {
                    flag = true
                }
                else{
                    Log.e("ProfileActivity", "Error getting user: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Connection Error 1", e)
            }
        }
        return flag
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
