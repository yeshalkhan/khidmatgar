
package com.khidmatgar.repositories

import android.content.Context
import android.content.SharedPreferences

class UserRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    companion object {
        var email: String? = null
    }

    init {
        email = sharedPreferences.getString("userEmail", null)
    }
}
