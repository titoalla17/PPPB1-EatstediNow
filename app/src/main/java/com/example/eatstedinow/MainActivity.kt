package com.example.eatstedinow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eatstedinow.screens.*
import com.example.eatstedinow.ui.theme.EatsTediNowTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            EatsTediNowTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (auth.currentUser != null) Routes.HOME else Routes.ONBOARDING
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {

            // --- AUTHENTICATION ---
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onLoginClick = { navController.navigate(Routes.LOGIN) },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onBackClick = { navController.popBackStack() },
                    onLoginSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = { navController.navigate(Routes.LOGIN) }
                )
            }

            // --- USER UTAMA ---
            composable(Routes.HOME) {
                HomeScreen(
                    onFoodClick = { id -> navController.navigate("${Routes.MENU_DETAIL}/$id") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onMenuClick = { category -> navController.navigate("${Routes.MENU}?category=$category") }
                )
            }

            composable(
                route = "${Routes.MENU}?category={category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType; defaultValue = "Semua"; nullable = true })
            ) { entry ->
                MenuScreen(
                    initialCategory = entry.arguments?.getString("category") ?: "Semua",
                    onFoodClick = { id -> navController.navigate("${Routes.MENU_DETAIL}/$id") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onHomeClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }
                )
            }

            composable(
                route = "${Routes.MENU_DETAIL}/{foodId}",
                arguments = listOf(navArgument("foodId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("foodId")
                if (id != null) {
                    MenuDetailScreen(foodId = id, onBack = { navController.popBackStack() }, onAddToCart = { navController.navigate(Routes.ORDER) })
                }
            }

            composable(Routes.ORDER) {
                OrderScreen(
                    onBack = { navController.popBackStack() },
                    onProcess = { navController.navigate(Routes.HISTORY) },
                    onHomeClick = { navController.navigate(Routes.HOME) },
                    onMenuClick = { navController.navigate("${Routes.MENU}?category=Semua") },
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = { auth.signOut(); navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
                    onHomeClick = { navController.navigate(Routes.HOME) },
                    onMenuClick = { navController.navigate("${Routes.MENU}?category=Semua") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onAdminClick = { navController.navigate(Routes.ADMIN_HOME) },
                    onHistoryClick = { navController.navigate(Routes.HISTORY) }
                )
            }

            composable(Routes.HISTORY) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }

            // --- ADMIN SECTION ---

            // 1. Admin Menu (Kelola Makanan)
            composable(Routes.ADMIN_HOME) {
                AdminMenuListScreen(
                    onAddClick = { navController.navigate(Routes.ADMIN_FORM) },
                    onEditClick = { id -> navController.navigate("${Routes.ADMIN_FORM}?foodId=$id") },
                    onVoucherClick = { navController.navigate(Routes.ADMIN_VOUCHER_LIST) }, // <--- CONNECTED HERE
                    onBack = { navController.popBackStack() }
                )
            }

            // 2. Admin Form (Tambah/Edit Makanan)
            composable(
                route = "${Routes.ADMIN_FORM}?foodId={foodId}",
                arguments = listOf(navArgument("foodId") { type = NavType.StringType; nullable = true })
            ) { entry ->
                AdminFormScreen(foodId = entry.arguments?.getString("foodId"), onBack = { navController.popBackStack() })
            }

            // 3. Admin Voucher List (BARU)
            composable(Routes.ADMIN_VOUCHER_LIST) {
                AdminVoucherListScreen(
                    onAddClick = { navController.navigate(Routes.ADMIN_VOUCHER_FORM) },
                    onEditClick = { id -> navController.navigate("${Routes.ADMIN_VOUCHER_FORM}?voucherId=$id") },
                    onBack = { navController.popBackStack() }
                )
            }

            // 4. Admin Voucher Form (BARU)
            composable(
                route = "${Routes.ADMIN_VOUCHER_FORM}?voucherId={voucherId}",
                arguments = listOf(navArgument("voucherId") { type = NavType.StringType; nullable = true })
            ) { entry ->
                AdminVoucherFormScreen(
                    voucherId = entry.arguments?.getString("voucherId"),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Next Step: Would you like to implement the logic for applying these vouchers on the Order/Cart screen (checking code, calculating discount)?