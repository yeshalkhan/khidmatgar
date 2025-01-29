package com.khidmatgar.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.khidmatgar.R
import com.khidmatgar.models.User
import com.khidmatgar.server_integration.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_profile)

        val firstName = findViewById<TextInputEditText>(R.id.first_name)
        val lastName = findViewById<TextInputEditText>(R.id.last_name)
        val phoneNo = findViewById<TextInputEditText>(R.id.phone_no)
        val email = findViewById<TextView>(R.id.email)
        val address = findViewById<TextInputEditText>(R.id.address)

        // Fetch and populate user data
        lifecycleScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                val userEmail = auth.currentUser?.email ?: ""
                val response = RetrofitInstance.api.getUserByEmail(userEmail)

                if (response.isSuccessful) {
                    val user = response.body()
                    withContext(Dispatchers.Main) {
                        firstName.setText(user?.firstName)
                        lastName.setText(user?.lastName)
                        phoneNo.setText(user?.phoneNo)
                        email.text = user?.email
                        address.setText(user?.location)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Error fetching user details", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Error fetching user", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Save profile changes
        findViewById<Button>(R.id.save_profile_changes).setOnClickListener {
            val updatedUser = User(
                email = email.text.toString(),
                firstName = firstName.text.toString(),
                lastName = lastName.text.toString(),
                location = address.text.toString(),
                phoneNo = phoneNo.text.toString()
            )

            // Save changes using lifecycleScope
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.updateUser(email.text.toString(), updatedUser)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProfileActivity, "Changes Saved Successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@ProfileActivity, "Failed to save changes", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Error saving changes", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Handle focus changes for EditText fields
        handleFocusChange(firstName, R.drawable.profile_input_background_selected, R.drawable.profile_input_background_unselected)
        handleFocusChange(lastName, R.drawable.profile_input_background_selected, R.drawable.profile_input_background_unselected)
        handleFocusChange(phoneNo, R.drawable.profile_input_background_selected, R.drawable.profile_input_background_unselected)
        handleFocusChange(address, R.drawable.profile_input_background_selected, R.drawable.profile_input_background_unselected)
    }

    private fun handleFocusChange(editText: TextInputEditText, focusDrawable: Int, unfocusDrawable: Int) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            val parentLayout = editText.parent as View
            if (hasFocus) {
                parentLayout.setBackgroundResource(focusDrawable)
            } else {
                parentLayout.setBackgroundResource(unfocusDrawable)
            }
        }
    }
}
