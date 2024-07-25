package com.example.fibertel.model

data class Factura(
    val id: String,
    val amount: String,
    val balance: String,
    val state: String,
    val invoice_number: String,
    val first_due_date: String,
    val second_due_date: String
)
