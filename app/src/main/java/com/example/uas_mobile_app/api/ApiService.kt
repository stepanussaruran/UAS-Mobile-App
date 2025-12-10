package com.example.uas_mobile_app.api

import com.example.uas_mobile_app.model.ApiResponse
import com.example.uas_mobile_app.model.Event
import com.example.uas_mobile_app.model.EventStats
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api.php")
    suspend fun getAllEvents(): Response<ApiResponse<List<Event>>>

    @GET("api.php")
    suspend fun getEventById(@Query("id") id: Int): Response<ApiResponse<Event>>

    @GET("api.php")
    suspend fun getStatistics(@Query("stats") stats: Int = 1): Response<ApiResponse<EventStats>>

    @POST("api.php")
    suspend fun createEvent(@Body event: Event): Response<ApiResponse<Event>>

    @PUT("api.php")
    suspend fun updateEvent(
        @Query("id") id: Int,
        @Body event: Event
    ): Response<ApiResponse<Event>>

    @DELETE("api.php")
    suspend fun deleteEvent(
        @Query("id") id: Int
    ): Response<ApiResponse<Unit>>
}