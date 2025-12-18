package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.example.eatstedinow.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showVoucherDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = onMenuClick)
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Order", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(
                    icon = {
                        Box {
                            Icon(Icons.Default.Person, "Profile")
                            if(profileState.pendingRatingCount > 0) Box(Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                        }
                    },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
            }
        }
    ) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))

            Box(Modifier.size(120.dp).border(3.dp, OrangePrimary, CircleShape).padding(4.dp)) {
                AsyncImage(model = profileState.photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(16.dp))
            Text(profileState.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(40.dp))

            SectionTitle("Account")
            ProfileMenuItem("Informasi Pribadi", onClick = { showEditDialog = true })
            ProfileMenuItem("Riwayat Pembelian", onClick = onHistoryClick, hasNotification = profileState.pendingRatingCount > 0)
            ProfileMenuItem("Voucher Saya", onClick = { showVoucherDialog = true })

            Spacer(Modifier.height(24.dp))
            SectionTitle("System")
            TextButton(onClick = onLogout, contentPadding = PaddingValues(0.dp), modifier = Modifier.align(Alignment.Start)) { Text("Log Out", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(40.dp))
            Button(onClick = onAdminClick, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("Mode Admin")
            }
            Spacer(Modifier.height(24.dp))
        }

        if (showVoucherDialog) {
            AlertDialog(
                onDismissRequest = { showVoucherDialog = false },
                title = { Text("Voucher Saya") },
                text = {
                    if (profileState.redeemedVouchers.isEmpty()) {
                        Text("Anda belum memiliki voucher.", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        Column(modifier = Modifier.fillMaxWidth().height(300.dp).verticalScroll(rememberScrollState())) {
                            profileState.redeemedVouchers.forEach { voucher ->
                                VoucherCard(voucher)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showVoucherDialog = false }) { Text("Tutup") } }
            )
        }

        if (showEditDialog) {
            EditProfileDialog(
                currentName = profileState.displayName,
                currentEmail = profileState.email,
                onDismiss = { showEditDialog = false },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun EditProfileDialog(currentName: String, currentEmail: String, onDismiss: () -> Unit, viewModel: MainViewModel) {
    var editName by remember { mutableStateOf(currentName) }
    var editEmail by remember { mutableStateOf(currentEmail) }
    var editMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Informasi Pengguna") },
        text = {
            Column {
                OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nama") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("Email") }, singleLine = true)
                if (editMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(editMessage, color = if(editMessage.contains("Gagal")) Color.Red else Color(0xFF388E3C), fontSize = 12.sp)
                }
                if (isSaving) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    isSaving = true
                    viewModel.updateProfile(editName, editEmail) { success, msg ->
                        isSaving = false
                        editMessage = msg
                        if (success && !msg.contains("verifikasi")) {
                            // Optional: Delay close or user closes manually
                        }
                    }
                }
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit, hasNotification: Boolean = false) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 16.sp)
            if (hasNotification) { Spacer(Modifier.width(8.dp)); Box(Modifier.size(8.dp).background(Color.Red, CircleShape)) }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Black)
    }
}

@Composable
fun VoucherCard(voucherCode: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Kode Voucher", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Text(voucherCode, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            }
            Icon(Icons.Default.ConfirmationNumber, null, tint = OrangePrimary, modifier = Modifier.size(40.dp))
        }
    }
}