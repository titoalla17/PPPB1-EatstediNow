package com.example.eatstedinow.model

data class FoodItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val originalPrice: Int? = null,
    val imageUrl: String,
    val rating: Double,
    val ratingCount: Int = 0,
    val category: String = "Makanan",
    val stock: Int = 10
)