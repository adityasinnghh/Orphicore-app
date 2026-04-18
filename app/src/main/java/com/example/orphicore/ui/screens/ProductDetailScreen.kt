package com.example.orphicore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.orphicore.viewmodel.CartViewModel
import com.example.orphicore.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel
) {

    val products by productViewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    product?.let {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Image(
                painter = rememberAsyncImagePainter(it.image),
                contentDescription = it.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(it.name, style = MaterialTheme.typography.headlineMedium)

            Text("₹${it.price}", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    cartViewModel.addToCart(it)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Cart 🛒")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate("cart")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Cart")
            }
        }
    }
}