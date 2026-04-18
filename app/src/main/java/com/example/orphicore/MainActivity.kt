package com.example.orphicore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.orphicore.ui.screens.*
import com.example.orphicore.ui.theme.OrphicoreTheme
import com.example.orphicore.viewmodel.CartViewModel
import com.example.orphicore.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orphicore.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OrphicoreTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // 🔥 Shared ViewModels
    val cartViewModel: CartViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("home") {
            HomeScreen(navController, productViewModel, cartViewModel)
        }

        composable("cart") {
            CartScreen(navController, cartViewModel)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignupScreen(navController)
        }
        composable("productDetails/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                navController,
                productId,
                productViewModel,
                cartViewModel
            )
        }
    }
}