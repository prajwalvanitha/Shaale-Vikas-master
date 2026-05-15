package com.example.shaalevikas.ui.components

data class Need(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val targetCost: Int,
    val fundsRaised: Int,
    val daysLeft: Int,
    val category: String,
    val isPriority: Boolean
)