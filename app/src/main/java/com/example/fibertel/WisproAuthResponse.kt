package com.example.fibertel

import com.google.gson.annotations.SerializedName

data class WisproAuthResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
)
