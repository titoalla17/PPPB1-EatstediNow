package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.example.eatstedinow.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    onFoodClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMenuClick: (String) -> Unit,
    onNotificationClick: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val homeState by viewModel.homeState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()

    // Logic Populer: Urutkan Rating Tertinggi, ambil 5 teratas
    val popularList = remember(homeState.menuList) {
        homeState.menuList.sortedByDescending { it.rating }.take(5)
    }
    // Logic Promo: Ambil yang punya originalPrice
    val promoList = remember(homeState.menuList) {
        homeState.menuList.filter { it.originalPrice != null && it.originalPrice > it.price }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = { onMenuClick("Semua") })
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Cart", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA)).padding(paddingValues).padding(horizontal = 16.dp)) {

            // Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HomeHeader(profileState.displayName, profileState.photoUrl, onNotificationClick)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Kategori
            item {
                Text("Kategori", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                CategorySection(onCategoryClick = { cat -> onMenuClick(cat) })
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sedang Populer (Top 5 Rating)
            item {
                Text("Sedang Populer \uD83D\uDD25", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (homeState.isLoading) {
                    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
                } else if (popularList.isEmpty()) {
                    Text("Belum ada data populer.", color = Color.Gray)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(popularList) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sedang Promo (Harga Coret)
            item {
                Text("Lagi Promo Hemat \uD83C\uDFF7", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (!homeState.isLoading) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(promoList) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --- KOMPONEN FOOD CARD DENGAN HARGA CORET ---
@Composable
fun FoodCardVertical(food: FoodItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(160.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(model = food.imageUrl, contentDescription = null, modifier = Modifier.size(136.dp).clip(CircleShape).align(Alignment.Center), contentScale = ContentScale.Crop)

                // Badge Promo
                if (food.originalPrice != null && food.originalPrice > food.price) {
                    Box(modifier = Modifier.align(Alignment.TopStart).background(Color.Red, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("PROMO", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Rating
                Box(modifier = Modifier.align(Alignment.BottomStart).background(Color.Black.copy(0.6f), RoundedCornerShape(12.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(10.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("${food.rating}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(food.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)

            // Logic Harga Coret
            if (food.originalPrice != null && food.originalPrice > food.price) {
                Text(
                    text = "Rp ${food.originalPrice}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }
            Text("Rp ${food.price}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
        }
    }
}

@Composable
fun HomeHeader(name: String, photoUrl: String, onNotificationClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(12.dp))
            Column { Text("Selamat datang!", fontSize = 12.sp, color = Color.Gray); Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
        IconButton(onClick = onNotificationClick) { Icon(Icons.Outlined.Notifications, null) }
    }
}

@Composable
fun CategorySection(onCategoryClick: (String) -> Unit) {
    val cats = listOf(
        Triple("Semua", Icons.Default.Fastfood, Color(0xFFE8F5E9)),
        Triple("Makanan", Icons.Default.LunchDining, Color(0xFFFFF3E0)),
        Triple("Minuman", Icons.Default.LocalDrink, Color(0xFFE3F2FD)),
        Triple("Snack", Icons.Default.Cookie, Color(0xFFFCE4EC))
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        cats.forEach { (name, icon, bg) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onCategoryClick(name) }) {
                Box(Modifier.size(60.dp).background(bg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.Black)
                }
                Spacer(Modifier.height(4.dp))
                Text(name, fontSize = 12.sp)
            }
        }
    }
}