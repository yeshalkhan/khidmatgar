package com.khidmatgar.server_integration

import com.khidmatgar.models.Booking
import com.khidmatgar.models.Bookmark
import com.khidmatgar.models.ServiceProvider
import com.khidmatgar.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // User

    @GET("api/user/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<User>

    @PUT("api/user/{email}")
    suspend fun updateUser(@Path("email") email: String, @Body user: User): Response<Void>

    @POST("api/user")
    suspend fun addUser(@Body user: User): Response<Void>

    @DELETE("api/user/{email}")
    suspend fun deleteUser(@Path("email") email: String): Response<Void>

    // Service Provider

    @GET("api/ServiceProviders")
    suspend fun getAllServiceProviders(): Response<MutableList<ServiceProvider>>

    @GET("api/ServiceProviders/{id}")
    suspend fun getServiceProviderById(@Path("id") id: Int): Response<ServiceProvider>

    @GET("api/ServiceProviders/search/{searchTerm}")
    suspend fun searchServiceProvider(@Path("searchTerm") searchTerm: String) : Response<MutableList<ServiceProvider>>

    // Booking

    @GET("api/Booking/{id}")
    suspend fun getBookingById(@Path("id") id: Int): Response<Booking>

    @GET("api/Booking/User/{userEmail}")
    suspend fun getBookingsByUserEmail(@Path("userEmail") userEmail: String): Response<List<Booking>>

    @POST("api/Booking")
    suspend fun createBooking(@Body booking: Booking): Response<Booking>

    @PUT("api/Booking/{id}")
    suspend fun updateBooking(@Path("id") id: Int, @Body booking: Booking): Response<Void>

    @DELETE("api/Booking/{id}")
    suspend fun deleteBooking(@Path("id") id: Int): Response<Void>

    // Bookmark

    @GET("api/Bookmark/User/{userEmail}")
    suspend fun getBookmarksByUserEmail(@Path("userEmail") userEmail: String): Response<List<Bookmark>>

    @POST("api/Bookmark")
    suspend fun addBookmark(@Body bookmark: Bookmark): Response<Void>

    @DELETE("api/Bookmark/{userEmail}/{serviceProviderId}")
    suspend fun deleteBookmark(@Path("userEmail") userEmail: String, @Path("serviceProviderId") serviceProviderId: Int): Response<Void>
}