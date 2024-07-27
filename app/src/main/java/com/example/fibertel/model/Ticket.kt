package com.example.fibertel.model

data class Ticket(
    val id: String,
    val publicId: Int,
    val clientId: String?,
    val contractId: String?,
    val categoryId: String,
    val assignableId: String?,
    val title: String,
    val description: String,
    val assignedAt: String?,
    val finalizedAt: String?,
    val closedAt: String?,
    val state: String,
    val createdAt: String,
    val updatedAt: String,
    val priority: String,
    val complexity: String,
    val expiresAt: String?
)