package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherFormScreen(
    voucherId: String?, // Null = Baru, Ada Isi = Edit
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val isEditMode = voucherId != null

    // State Form
    var code by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var minPurchase by remember { mutableStateOf("") }
    var quota by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Load Data jika Edit Mode
    LaunchedEffect(voucherId) {
        if (voucherId != null) {
            db.collection("vouchers").document(voucherId).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    code = doc.getString("code") ?: ""
                    discount = (doc.getLong("discount") ?: 0).toString()
                    minPurchase = (doc.getLong("minPurchase") ?: 0).toString()
                    quota = (doc.getLong("quota") ?: 0).toString()
                    description = doc.getString("description") ?: ""
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Voucher" else "Buat Voucher") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = {
                            isLoading = true
                            db.collection("vouchers").document(voucherId!!).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Voucher Dihapus!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        }) {
                            Icon(Icons.Default.Delete, "Hapus", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp).verticalScroll(rememberScrollState())) {

            // Input Kode (Auto Uppercase biar rapi)
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                label = { Text("Kode Voucher (Unik)") },
                placeholder = { Text("CONTOH: MABA2024") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Input Diskon & Min Belanja
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text("Diskon (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minPurchase,
                    onValueChange = { minPurchase = it },
                    label = { Text("Min. Belanja (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Input Kuota & Deskripsi
            OutlinedTextField(
                value = quota,
                onValueChange = { quota = it },
                label = { Text("Jumlah Kuota") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Kriteria / Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (code.isNotEmpty() && discount.isNotEmpty() && quota.isNotEmpty()) {
                        isLoading = true
                        val data = hashMapOf(
                            "code" to code.trim().uppercase(),
                            "discount" to (discount.toIntOrNull() ?: 0),
                            "minPurchase" to (minPurchase.toIntOrNull() ?: 0),
                            "quota" to (quota.toIntOrNull() ?: 0),
                            "description" to description
                        )

                        if (isEditMode) {
                            db.collection("vouchers").document(voucherId!!).update(data as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Voucher Diupdate!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        } else {
                            db.collection("vouchers").add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Voucher Dibuat!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Kode, Diskon, dan Kuota wajib diisi!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("SIMPAN VOUCHER")
            }
        }
    }
}