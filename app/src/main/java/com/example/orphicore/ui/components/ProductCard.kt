package com.example.orphicore.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.orphicore.data.model.Product

@Composable
fun ProductCard(product: Product, onAddClick: () -> Unit) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(product.name, style = MaterialTheme.typography.titleMedium)

            Text("₹${product.price}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onAddClick) {
                Text("Add to Cart")
            }
        }
    }
}