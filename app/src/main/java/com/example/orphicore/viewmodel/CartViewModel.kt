package com.example.orphicore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.orphicore.data.model.CartItem
import com.example.orphicore.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // ➕ Add to cart
    fun addToCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()

        val existingItem = currentList.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentList.add(CartItem(product))
        }

        _cartItems.value = currentList
    }

    // ➖ Remove from cart
    fun removeFromCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()

        val existingItem = currentList.find { it.product.id == product.id }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                currentList.remove(existingItem)
            }
        }

        _cartItems.value = currentList
    }

    // 🗑 Remove completely
    fun removeItemCompletely(product: Product) {
        val updatedList = _cartItems.value.filter {
            it.product.id != product.id
        }
        _cartItems.value = updatedList
    }

    // 💰 Total price
    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf {
            it.product.price * it.quantity
        }
    }

    // 🚀 Place Order (Firestore)
    fun placeOrder(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val orderMap = hashMapOf(
            "items" to _cartItems.value.map {
                hashMapOf(
                    "name" to it.product.name,
                    "price" to it.product.price,
                    "quantity" to it.quantity
                )
            },
            "total" to getTotalPrice(),
            "userId" to (auth.currentUser?.uid ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("orders")
            .add(orderMap)
            .addOnSuccessListener {
                _cartItems.value = emptyList() // clear cart
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Order failed")
            }
    }
}