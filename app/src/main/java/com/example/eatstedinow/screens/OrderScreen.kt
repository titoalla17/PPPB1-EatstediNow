package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eatstedinow.CartState
import com.example.eatstedinow.model.Voucher
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
// --- FIX: IMPORT INI WAJIB ADA ---
import com.google.firebase.firestore.FirebaseFirestoreException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    onProcess: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    // State Input Voucher
    var voucherInput by remember { mutableStateOf("") }
    var voucherLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = OrangePrimary) } }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = onMenuClick)
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Order") }, label = { Text("Order", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        if (CartState.items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Keranjang Kosong", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {

                // --- INFO MEJA (HANYA MUNCUL JIKA DINE IN) ---
                if (CartState.isDineIn) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TableRestaurant, null, tint = Color.Blue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Makan di Tempat", fontWeight = FontWeight.Bold)
                                    Text("Nomor: ${CartState.tableNumber}", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { /* Logic Ganti Meja */ }) { Text("Ganti") }
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShoppingBag, null, tint = OrangePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Pesanan Take Away (Bungkus)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(CartState.items) { item ->
                    CartItemCard(
                        item = item,
                        onAdd = { CartState.addQuantity(item) },
                        onRemove = { CartState.removeItem(item) }
                    )
                }

                // Voucher Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Punya Voucher?", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (CartState.appliedVoucher == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = voucherInput,
                                onValueChange = { voucherInput = it.uppercase() },
                                placeholder = { Text("Masukkan kode") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (voucherInput.isEmpty()) return@Button
                                    voucherLoading = true
                                    db.collection("vouchers")
                                        .whereEqualTo("code", voucherInput.trim())
                                        .get()
                                        .addOnSuccessListener { snapshot ->
                                            voucherLoading = false
                                            if (!snapshot.isEmpty) {
                                                val doc = snapshot.documents.first()
                                                val v = Voucher(
                                                    id = doc.id,
                                                    code = doc.getString("code") ?: "",
                                                    discount = doc.getLong("discount")?.toInt() ?: 0,
                                                    minPurchase = doc.getLong("minPurchase")?.toInt() ?: 0,
                                                    quota = doc.getLong("quota")?.toInt() ?: 0
                                                )
                                                if (v.quota <= 0) {
                                                    Toast.makeText(context, "Kuota voucher habis!", Toast.LENGTH_SHORT).show()
                                                } else if (CartState.subtotal < v.minPurchase) {
                                                    Toast.makeText(context, "Min. belanja Rp${v.minPurchase}", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    CartState.appliedVoucher = v
                                                    Toast.makeText(context, "Voucher dipasang!", Toast.LENGTH_SHORT).show()
                                                    voucherInput = ""
                                                }
                                            } else {
                                                Toast.makeText(context, "Kode tidak ditemukan", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            voucherLoading = false
                                            Toast.makeText(context, "Gagal cek voucher", Toast.LENGTH_SHORT).show()
                                        }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !voucherLoading
                            ) {
                                if (voucherLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Pakai")
                            }
                        }
                    } else {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(CartState.appliedVoucher!!.code, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text("Hemat Rp${CartState.discountAmount}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                }
                                IconButton(onClick = { CartState.appliedVoucher = null }) {
                                    Icon(Icons.Default.Close, "Hapus", tint = Color.Red)
                                }
                            }
                        }
                    }
                }

                // Summary & Pay
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    PaymentSummarySection()
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (userId == null) {
                                Toast.makeText(context, "Login dulu!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val orderRef = db.collection("orders").document()
                            val voucher = CartState.appliedVoucher

                            db.runTransaction { transaction ->
                                // Kurangi Stok
                                CartState.items.forEach { cartItem ->
                                    if (cartItem.food.id.length > 5) {
                                        val foodRef = db.collection("menus").document(cartItem.food.id)
                                        val snapshot = transaction.get(foodRef)
                                        val currentStock = snapshot.getLong("stock") ?: 0
                                        if (currentStock >= cartItem.quantity) {
                                            transaction.update(foodRef, "stock", currentStock - cartItem.quantity)
                                        } else {
                                            // FIX: Gunakan class Exception langsung
                                            throw FirebaseFirestoreException("Stok ${cartItem.food.name} habis!", FirebaseFirestoreException.Code.ABORTED)
                                        }
                                    }
                                }
                                // Kurangi Voucher
                                if (voucher != null) {
                                    val voucherRef = db.collection("vouchers").document(voucher.id)
                                    val vSnap = transaction.get(voucherRef)
                                    val currentQuota = vSnap.getLong("quota") ?: 0
                                    if (currentQuota > 0) {
                                        transaction.update(voucherRef, "quota", currentQuota - 1)
                                    } else {
                                        // FIX: Gunakan class Exception langsung
                                        throw FirebaseFirestoreException("Voucher habis!", FirebaseFirestoreException.Code.ABORTED)
                                    }
                                }
                                // Simpan Order
                                val orderData = hashMapOf(
                                    "userId" to userId,
                                    "total" to CartState.total,
                                    "subtotal" to CartState.subtotal,
                                    "discount" to CartState.discountAmount,
                                    "orderType" to if (CartState.isDineIn) "Dine In (${CartState.tableNumber})" else "Take Away",
                                    "date" to FieldValue.serverTimestamp(),
                                    "isRated" to false,
                                    "items" to CartState.items.map {
                                        mapOf(
                                            "foodId" to it.food.id,
                                            "name" to it.food.name,
                                            "price" to it.food.price,
                                            "quantity" to it.quantity,
                                            "imageUrl" to it.food.imageUrl
                                        )
                                    }
                                )
                                transaction.set(orderRef, orderData)
                            }.addOnSuccessListener {
                                Toast.makeText(context, "Order Berhasil!", Toast.LENGTH_LONG).show()
                                CartState.clear()
                                onProcess()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Bayar & Proses", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: com.example.eatstedinow.CartState.CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.food.imageUrl, contentDescription = null, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text(item.food.category, fontSize = 12.sp, color = Color.Gray)
                Text("Rp ${item.food.price}", fontWeight = FontWeight.Bold, color = OrangePrimary)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) { Text("-", fontWeight = FontWeight.Bold) }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) { Text("+", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun PaymentSummarySection() {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Subtotal", color = Color.Gray); Text("RP ${CartState.subtotal}", color = Color.Gray) }
        if (CartState.discountAmount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Diskon Voucher", color = Color(0xFF2E7D32));
                Text("- RP ${CartState.discountAmount}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Biaya Layanan", color = Color.Gray); Text("RP ${CartState.tax}", color = Color.Gray) }
        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp);
            Text("RP ${CartState.total}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary)
        }
    }
}