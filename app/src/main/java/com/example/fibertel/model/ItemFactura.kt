package com.example.fibertel.model

data class ItemFactura(
    val id: String,
    val invoice_id: String,
    val quantity: Int,
    val description: String,
    val unit_price: Double,
    val total: Double
)