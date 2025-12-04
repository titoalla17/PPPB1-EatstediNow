package com.example.eatstedinow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.KeyboardArrowRight

@Composable
fun ProfileScreen(onLogout: () -> Unit, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Foto Profil
        Box(modifier = Modifier.size(100.dp).background(Color(0xFFFF8C00), CircleShape).padding(2.dp)) {
            Image(painter = painterResource(id = android.R.drawable.sym_def_app_icon), contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tito Alla", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Items
        ProfileItem("Informasi Pribadi")
        ProfileItem("Riwayat Pembelian")

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onLogout) {
            Text("Log Out", color = Color.Red)
        }

        // Bottom Bar Placeholder agar konsisten (manual add or reuse Scaffold)
    }
}

@Composable
fun ProfileItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}