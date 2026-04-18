package com.example.orphicore.data.repository

import com.example.orphicore.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ProductRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("products")   // ⚠️ must match EXACT name
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }
}
//implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")