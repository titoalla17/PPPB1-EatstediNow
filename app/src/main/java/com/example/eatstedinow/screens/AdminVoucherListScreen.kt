package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.model.Voucher
import com.example.eatstedinow.model.dummyVouchers
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherListScreen(
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var voucherList by remember { mutableStateOf<List<Voucher>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Load Data Realtime dari collection "vouchers"
    LaunchedEffect(Unit) {
        db.collection("vouchers").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                voucherList = snapshot.documents.mapNotNull { doc ->
                    try {
                        Voucher(
                            id = doc.id,
                            code = doc.getString("code") ?: "",
                            description = doc.getString("description") ?: "",
                            discount = doc.getLong("discount")?.toInt() ?: 0,
                            minPurchase = doc.getLong("minPurchase")?.toInt() ?: 0,
                            quota = doc.getLong("quota")?.toInt() ?: 0
                        )
                    } catch (e: Exception) { null }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Voucher") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    // 1. UPLOAD DUMMY VOUCHER
                    IconButton(onClick = {
                        isLoading = true
                        val batch = db.batch()
                        dummyVouchers.forEach { v ->
                            val docRef = db.collection("vouchers").document()
                            val data = hashMapOf(
                                "code" to v.code,
                                "description" to v.description,
                                "discount" to v.discount,
                                "minPurchase" to v.minPurchase,
                                "quota" to v.quota
                            )
                            batch.set(docRef, data)
                        }
                        batch.commit().addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Voucher Kampus Ditambahkan!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.CloudUpload, "Dummy", tint = OrangePrimary)
                    }

                    // 2. HAPUS SEMUA
                    IconButton(onClick = {
                        db.collection("vouchers").get().addOnSuccessListener { snapshot ->
                            val batch = db.batch()
                            snapshot.documents.forEach { batch.delete(it.reference) }
                            batch.commit()
                            Toast.makeText(context, "Semua Voucher Dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.DeleteForever, "Clear", tint = Color.Red)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = OrangePrimary) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { p ->
        Box(modifier = Modifier.padding(p).fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                if (voucherList.isEmpty()) {
                    item { Text("Belum ada voucher aktif.", color = Color.Gray) }
                }

                items(voucherList) { voucher ->
                    VoucherAdminCard(voucher, onEdit = { onEditClick(voucher.id) })
                }
            }

            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun VoucherAdminCard(voucher: Voucher, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ConfirmationNumber, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(voucher.code, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary)
                }
                Spacer(Modifier.height(4.dp))
                Text(voucher.description, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgeInfo("Diskon: Rp${voucher.discount}")
                    BadgeInfo("Sisa: ${voucher.quota}")
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun BadgeInfo(text: String) {
    Box(modifier = Modifier.background(Color(0xFFFFF3E0), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(text, fontSize = 10.sp, color = Color(0xFFE65100))
    }
}