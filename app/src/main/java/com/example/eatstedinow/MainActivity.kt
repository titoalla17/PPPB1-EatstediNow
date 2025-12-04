// MainActivity.kt
package com.example.eatstedinow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eatstedinow.ui.theme.EatsTediNowTheme

// Daftar Nama Halaman (Route)
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val MENU_DETAIL = "menu_detail/{foodId}"
    const val ORDER = "order"
    const val PROFILE = "profile"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatsTediNowTheme { // Pastikan nama theme sesuai projectmu
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        // 1. Halaman Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.HOME) }
            )
        }

        // 2. Halaman Home
        composable(Routes.HOME) {
            HomeScreen(
                onFoodClick = { foodId -> navController.navigate("menu_detail/$foodId") },
                onCartClick = { navController.navigate(Routes.ORDER) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // 3. Halaman Detail Menu
        composable(Routes.MENU_DETAIL) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull()
            val food = dummyFoods.find { it.id == foodId }
            if (food != null) {
                MenuDetailScreen(
                    food = food,
                    onBack = { navController.popBackStack() },
                    onAddToCart = { navController.navigate(Routes.ORDER) }
                )
            }
        }

        // 4. Halaman Order (Tugas Utama Kamu)
        composable(Routes.ORDER) {
            OrderScreen(
                onBack = { navController.popBackStack() },
                onProcess = { /* Logic Bayar nanti */ }
            )
        }

        // 5. Halaman Profile
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    }
}