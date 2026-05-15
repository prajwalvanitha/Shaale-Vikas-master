package com.example.shaalevikas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NeedCard(
    need: Need,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    val db = FirebaseFirestore.getInstance()

    Card(
        modifier = modifier.width(280.dp).padding(end = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = need.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                    contentScale = ContentScale.Crop,
                    loading = { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF0D9488)) } },
                    error = { Box(Modifier.fillMaxSize().background(Color(0xFFF1F5F9)), Alignment.Center) { Icon(Icons.Default.Warning, null, tint = Color.Red) } }
                )

                // Icons Layout
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (isAdmin) {
                        IconButton(
                            onClick = { db.collection("needs").document(need.id).delete() },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(36.dp)
                        ) { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp)) }
                    } else { Spacer(Modifier.size(1.dp)) }

                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(36.dp)
                    ) { Icon(if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (isSaved) Color.Red else Color.Gray, modifier = Modifier.size(18.dp)) }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(need.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B), maxLines = 1)

                Spacer(modifier = Modifier.height(12.dp))

                // Modern Progress Bar
                val progress = if (need.targetCost > 0) need.fundsRaised.toFloat() / need.targetCost else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF0D9488),
                    trackColor = Color(0xFFF1F5F9)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Raised", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("₹${need.fundsRaised}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D9488))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("₹${need.targetCost}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    }
                }
            }
        }
    }
}
