package com.example.fibertel.model

import com.google.gson.annotations.SerializedName

data class  User(
    @SerializedName("id")
    val id: String, // UUID en formato String

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    val phone: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("national_identification_number")
    val nationalIdentificationNumber: String,

    @SerializedName("link_mobile_login")
    val linkMobileLogin: String
)
