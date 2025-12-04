package com.example.eatstedinow

data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String, // Bisa pakai URL internet atau resource ID
    val rating: Double
)

// Data Dummy
val dummyFoods = listOf(
    FoodItem(1, "Cireng", "Cireng adalah makanan ringan tradisional Sunda...", 2500, "https://via.placeholder.com/150", 4.9),
    FoodItem(2, "Tahu Bakso", "Tahu bakso enak gurih nyoy...", 2500, "https://via.placeholder.com/150", 4.8),
    FoodItem(3, "Es Teh", "Seger banget...", 3000, "https://via.placeholder.com/150", 4.5)
)