package com.example.eatstedinow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun HomeScreen(onFoodClick: (Int) -> Unit, onCartClick: () -> Unit, onProfileClick: () -> Unit) {
    Scaffold(
        bottomBar = {
            // Bottom Bar sederhana
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "") }, label = { Text("Home") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "") }, label = { Text("Order") }, selected = false, onClick = onCartClick)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "") }, label = { Text("Profile") }, selected = false, onClick = onProfileClick)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Text("Halo, Tito Alla!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                // Kategori (Bisa dibuat Row scrollable)
                Text("Sedang Populer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Grid Makanan (Sederhana pakai Column utk contoh)
            items(dummyFoods) { food ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onFoodClick(food.id) },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = food.imageUrl, contentDescription = null, modifier = Modifier.size(60.dp).clip(CircleShape))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(food.name, fontWeight = FontWeight.Bold)
                            Text("Rp ${food.price}", color = Color(0xFF008000))
                        }
                    }
                }
            }
        }
    }
}