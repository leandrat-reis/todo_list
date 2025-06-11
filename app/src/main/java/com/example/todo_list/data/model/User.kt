package com.example.todoapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val username: String,
    val password: String,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null
)