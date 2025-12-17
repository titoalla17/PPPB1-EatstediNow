package com.example.eatstedinow.model

val dummyVouchers = listOf(
    Voucher(code = "HEMATKANTIN", description = "Diskon jajan hemat akhir bulan", discount = 5000, minPurchase = 20000, quota = 50),
    Voucher(code = "TRAKTIRTEMAN", description = "Potongan besar untuk makan bareng", discount = 10000, minPurchase = 50000, quota = 20),
    Voucher(code = "JUMATBERKAH", description = "Potongan spesial hari Jumat", discount = 2000, minPurchase = 10000, quota = 100),
    Voucher(code = "KOPIPAGI", description = "Diskon khusus pembelian minuman", discount = 3000, minPurchase = 15000, quota = 30)
)