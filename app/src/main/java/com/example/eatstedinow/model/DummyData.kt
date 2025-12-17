package com.example.eatstedinow.model

val dummyFoods = listOf(
    // Parameter: (id, name, description, price, originalPrice?, imageUrl, rating, ratingCount?, category?, stock?)

    // ===== Snack (Kata kunci: gorengan, snack) =====
    FoodItem("S1", "Cireng", "Cireng renyah isi bumbu.", 2000, 2500, "https://loremflickr.com/400/400/fried,food?random=1", 4.2, 125, "Snack", 50),
    FoodItem("S2", "Tahu Bakso", "Tahu isi bakso sapi.", 2500, null, "https://loremflickr.com/400/400/meatball,tofu?random=2", 4.5, 89, "Snack", 40),
    FoodItem("S3", "Pisang Goreng", "Pisang goreng manis.", 4000, null, "https://loremflickr.com/400/400/banana,fried?random=3", 4.7, 203, "Snack", 30),
    FoodItem("S4", "Risoles", "Risoles mayo ayam.", 2800, 3500, "https://loremflickr.com/400/400/risoles,snack?random=4", 4.0, 76, "Snack", 20),
    FoodItem("S5", "Bakwan", "Bakwan sayur crispy.", 2000, null, "https://loremflickr.com/400/400/fritters,vegetable?random=5", 4.3, 142, "Snack", 50),

    // ===== Minuman (Kata kunci: drink, tea, coffee) =====
    FoodItem("M1", "Es Teh Manis", "Teh manis segar.", 3000, null, "https://loremflickr.com/400/400/iced,tea?random=6", 4.1, 187, "Minuman", 100),
    FoodItem("M2", "Es Jeruk", "Jeruk peras asli.", 3500, 4000, "https://loremflickr.com/400/400/orange,juice?random=7", 4.6, 95, "Minuman", 80),
    FoodItem("M3", "Kopi Hitam", "Kopi panas nikmat.", 5000, null, "https://loremflickr.com/400/400/black,coffee?random=8", 4.4, 123, "Minuman", 50),
    FoodItem("M4", "Teh Tarik", "Teh susu creamy.", 5000, 6000, "https://loremflickr.com/400/400/milk,tea?random=9", 4.8, 210, "Minuman", 40),
    FoodItem("M5", "Susu Coklat", "Susu coklat dingin.", 5000, null, "https://loremflickr.com/400/400/chocolate,milk?random=10", 4.5, 178, "Minuman", 60),

    // ===== Makanan (Kata kunci: fried rice, noodle, chicken) =====
    FoodItem("F1", "Nasi Goreng", "Nasi goreng spesial telur.", 10000, 12000, "https://loremflickr.com/400/400/fried,rice?random=11", 4.5, 345, "Makanan", 25),
    FoodItem("F2", "Mie Goreng", "Mie goreng pedas.", 10000, null, "https://loremflickr.com/400/400/fried,noodle?random=12", 4.3, 267, "Makanan", 25),
    FoodItem("F3", "Ayam Geprek", "Ayam crispy sambal.", 13000, 15000, "https://loremflickr.com/400/400/fried,chicken,chili?random=13", 4.9, 412, "Makanan", 30),
    FoodItem("F4", "Seblak", "Seblak kerupuk pedas.", 10000, 12000, "https://loremflickr.com/400/400/spicy,soup?random=14", 4.6, 189, "Makanan", 20),
    FoodItem("F5", "Bakso", "Bakso kuah gurih.", 13000, null, "https://loremflickr.com/400/400/meatball,soup?random=15", 4.7, 301, "Makanan", 35),

    // ===== Es Krim (Kata kunci: ice cream) =====
    FoodItem("E1", "Es Krim Coklat", "Es krim coklat manis.", 5000, null, "https://loremflickr.com/400/400/chocolate,icecream?random=16", 4.4, 156, "Es Krim", 15),
    FoodItem("E2", "Es Krim Vanila", "Vanila lembut.", 4500, 5000, "https://loremflickr.com/400/400/vanilla,icecream?random=17", 4.5, 134, "Es Krim", 15),
    FoodItem("E3", "Es Krim Stroberi", "Segar rasa stroberi.", 5500, null, "https://loremflickr.com/400/400/strawberry,icecream?random=18", 4.3, 98, "Es Krim", 15),
    FoodItem("E4", "Es Krim Matcha", "Matcha creamy.", 5500, 6000, "https://loremflickr.com/400/400/matcha,icecream?random=19", 4.7, 167, "Es Krim", 10),
    FoodItem("E5", "Es Krim Cookies", "Cookies & cream.", 6000, null, "https://loremflickr.com/400/400/cookies,icecream?random=20", 4.8, 201, "Es Krim", 10)
)