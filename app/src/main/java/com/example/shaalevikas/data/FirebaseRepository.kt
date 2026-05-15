package com.example.shaalevikas.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // This function now only needs the text details. It generates the image URL itself.
    suspend fun uploadNeedWithoutImage(
        title: String,
        desc: String,
        cost: Int,
        category: String
    ): Boolean {
        return try {
            // Assign a high-quality stock photo URL based on the selected category
            val autoImageUrl = when(category) {
                "Medical" -> "https://unsplash.com"
                "Education" -> "https://unsplash.com"
                "Sports" -> "https://unsplash.com"
                else -> "https://unsplash.com"
            }

            val needData = hashMapOf(
                "title" to title,
                "description" to desc,
                "targetCost" to cost,
                "fundsRaised" to 0,
                "category" to category,
                "imageUrl" to autoImageUrl,
                "isPriority" to (cost > 50000)
            )

            // Saves only to the database, which is fast and reliable
            firestore.collection("needs").add(needData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
