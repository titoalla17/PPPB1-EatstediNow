package com.example.eatstedinow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.Voucher

object CartState {
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    var appliedVoucher by mutableStateOf<Voucher?>(null)

    // --- STATE BARU: DINE IN / TAKE AWAY ---
    var isDineIn by mutableStateOf(true) // Default Dine In
    var tableNumber by mutableStateOf("Meja 01") // Default Meja

    data class CartItem(val food: FoodItem, var quantity: Int)

    val subtotal: Int get() = _items.sumOf { it.food.price * it.quantity }
    val tax: Int get() = if (_items.isNotEmpty()) 2000 else 0

    val discountAmount: Int get() {
        val v = appliedVoucher ?: return 0
        return if (subtotal >= v.minPurchase) v.discount else 0
    }

    val total: Int get() = (subtotal + tax - discountAmount).coerceAtLeast(0)

    fun addItem(food: FoodItem) {
        val existing = _items.find { it.food.id == food.id }
        if (existing != null) {
            val index = _items.indexOf(existing)
            _items[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            _items.add(CartItem(food, 1))
        }
        validateVoucher()
    }

    fun removeItem(item: CartItem) {
        val existing = _items.find { it.food.id == item.food.id } ?: return
        if (existing.quantity > 1) {
            val index = _items.indexOf(existing)
            _items[index] = existing.copy(quantity = existing.quantity - 1)
        } else {
            _items.remove(existing)
        }
        validateVoucher()
    }

    fun addQuantity(item: CartItem) {
        val index = _items.indexOf(item)
        if (index != -1) {
            _items[index] = item.copy(quantity = item.quantity + 1)
        }
        validateVoucher()
    }

    private fun validateVoucher() {
        val v = appliedVoucher
        if (v != null && subtotal < v.minPurchase) {
            appliedVoucher = null
        }
    }

    fun clear() {
        _items.clear()
        appliedVoucher = null
        // Kita tidak reset isDineIn agar user tidak perlu milih lagi jika order ulang
    }
}