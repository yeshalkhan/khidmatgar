package com.khidmatgar.server_integration

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.*
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {

//   const val BASE_URL = "http://10.0.2.2:5035/" // Localhost for Android emulator
    const val BASE_URL = "http://192.168.1.106:5035/" // Localhost for Physical device
//    const val BASE_URL = "https://kidmatgarapi.azurewebsites.net/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

}
