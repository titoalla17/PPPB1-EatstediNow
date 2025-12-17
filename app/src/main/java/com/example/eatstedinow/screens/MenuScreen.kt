package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.CartState // Import CartState
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MenuScreen(
    initialCategory: String = "Semua",
    onFoodClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    val categories = listOf("Semua", "Makanan", "Minuman", "Es Krim", "Snack")

    var menuList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("menus").addSnapshotListener { snapshot, e ->
            if (e != null) { isLoading = false; return@addSnapshotListener }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getLong("price")?.toInt() ?: 0,
                            originalPrice = doc.getLong("originalPrice")?.toInt(),
                            imageUrl = doc.getString("imageUrl") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            category = doc.getString("category") ?: "Makanan",
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    } catch (e: Exception) { null }
                }
                menuList = items
                isLoading = false
            }
        }
    }

    val sourceData = if (!isLoading && menuList.isEmpty()) dummyFoods else menuList

    val filteredMenu = remember(sourceData, selectedCategory) {
        if (selectedCategory == "Semua") {
            sourceData.sortedByDescending { it.rating }
        } else {
            sourceData.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Order", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { p ->
        Column(modifier = Modifier.fillMaxSize().padding(p).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(if(selectedCategory=="Semua") "Menu Populer" else "Menu $selectedCategory", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // --- TOGGLE DINE IN / TAKE AWAY ---
            // Mengubah CartState secara langsung
            Row(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(20.dp)).padding(4.dp)) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .background(if (CartState.isDineIn) Color.White else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { CartState.isDineIn = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Dine In", fontWeight = if (CartState.isDineIn) FontWeight.Bold else FontWeight.Normal)
                }
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .background(if (!CartState.isDineIn) Color.White else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { CartState.isDineIn = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Take Away", fontWeight = if (!CartState.isDineIn) FontWeight.Bold else FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB KATEGORI
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) OrangePrimary else Color.LightGray.copy(0.3f))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(cat, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && menuList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredMenu) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}