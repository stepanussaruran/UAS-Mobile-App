package com.example.uas_mobile_app.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("location") val location: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("status") val status: String
)

data class EventStats(
    @SerializedName("total") val total: Int,
    @SerializedName("upcoming") val upcoming: Int,
    @SerializedName("ongoing") val ongoing: Int,
    @SerializedName("completed") val completed: Int,
    @SerializedName("cancelled") val cancelled: Int
)

data class ApiResponse<T>(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?,
    @SerializedName("timestamp") val timestamp: String?
)