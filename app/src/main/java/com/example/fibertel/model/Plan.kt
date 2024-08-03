package com.example.fibertel.model

data class Plan(
    val name: String,
    val public_id: Int,
    val ceil_down_kbps: Int,
    val ceil_up_kbps: Int,
    val price: String,
    val created_at: String,
    val updated_at: String
)

