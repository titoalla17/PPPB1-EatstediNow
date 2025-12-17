package com.example.eatstedinow.model

data class Voucher(
    val id: String = "",
    val code: String = "",         // Contoh: "MABA2024"
    val description: String = "",  // Contoh: "Khusus Mahasiswa Baru"
    val discount: Int = 0,         // Contoh: 5000 (Rupiah)
    val minPurchase: Int = 0,      // Contoh: 20000 (Syarat minimal beli)
    val quota: Int = 0             // Contoh: 100 (Batas pemakaian)
)
