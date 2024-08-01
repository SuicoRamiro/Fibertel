package com.example.fibertel

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object ApiClient {
    private const val BASE_URL = "https://www.cloud.wispro.co/api/v1/"
    private const val API_TOKEN = "2865daf0-f236-46cf-b3c9-5ef541183a31"

    private val client = OkHttpClient()

    fun createRequest(endpoint: String): Request {
        return Request.Builder()
            .url("$BASE_URL$endpoint")
            .header("Authorization", API_TOKEN)
            .build()
    }

    fun createPatchRequest(endpoint: String, requestBody: RequestBody): Request {
        return Request.Builder()
            .url("$BASE_URL$endpoint")
            .header("Authorization", API_TOKEN)
            .patch(requestBody)
            .build()
    }

    fun getClient(): OkHttpClient = client
}

