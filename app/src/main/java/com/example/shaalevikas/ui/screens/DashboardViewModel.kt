package com.example.shaalevikas.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shaalevikas.ui.components.Need
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val needsList = mutableStateListOf<Need>()
    val savedNeedsIds = mutableStateListOf<String>()

    var selectedCategory by mutableStateOf("All")

    var userName by mutableStateOf("User")
        private set

    // 🛡️ Default to FALSE for every new instance
    var isMember by mutableStateOf(false)
        private set

    init {
        refreshUserStatus()
        fetchNeeds()
    }

    // --- THE RESET AND REFRESH FUNCTION ---
    fun refreshUserStatus() {
        val user = auth.currentUser
        val userEmail = user?.email
        val userId = user?.uid

        // 1. CLEAR OLD DATA: Immediate reset before fetching new data
        isMember = false
        userName = "User"

        // 2. ADMIN CHECK: If it's your email, grant instant access
        if (userEmail == "prajwalbhat@shaalevikas.com") {
            isMember = true
            userName = "Prajwal Bhat"
            return
        }

        // 3. FIRESTORE FETCH: Get specific data for standard users
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        userName = snapshot.getString("username") ?: "User"
                        // Standard users must have 'isMember' true in Firestore
                        isMember = snapshot.getBoolean("isMember") ?: false
                    } else {
                        // Document doesn't exist yet (e.g. fresh Google Sign-in)
                        userName = user.displayName ?: "User"
                        isMember = false
                    }
                }
                .addOnFailureListener {
                    isMember = false
                }
        }
    }

    fun toggleSaveNeed(needId: String) {
        if (savedNeedsIds.contains(needId)) {
            savedNeedsIds.remove(needId)
        } else {
            savedNeedsIds.add(needId)
        }
    }

    fun fetchNeeds() {
        viewModelScope.launch {
            db.collection("needs").addSnapshotListener { value, _ ->
                needsList.clear()
                value?.documents?.forEach { doc ->
                    val need = Need(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        targetCost = doc.getLong("targetCost")?.toInt() ?: 0,
                        fundsRaised = doc.getLong("fundsRaised")?.toInt() ?: 0,
                        category = doc.getString("category") ?: "All",
                        daysLeft = 30,
                        isPriority = false
                    )
                    needsList.add(need)
                }
            }
        }
    }
}
