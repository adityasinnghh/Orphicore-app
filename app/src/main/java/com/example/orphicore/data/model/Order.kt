package com.example.orphicore.data.model

data class Order(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)