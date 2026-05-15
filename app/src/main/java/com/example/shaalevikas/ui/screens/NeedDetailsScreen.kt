package com.example.shaalevikas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.shaalevikas.ui.components.Need
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedDetailsScreen(
    need: Need,
    isMember: Boolean,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var customAmount by remember { mutableStateOf("") }
    var isPledging by remember { mutableStateOf(false) }

    val TealMain = Color(0xFF0D9488)

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- HERO IMAGE ---
        SubcomposeAsyncImage(
            model = need.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(400.dp),
            contentScale = ContentScale.Crop,
            loading = { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = TealMain) } },
            error = { Box(Modifier.fillMaxSize().background(Color.LightGray), Alignment.Center) { Icon(Icons.Default.Warning, null, tint = Color.Red) } }
        )

        // Gradient Overlay on Image for better text visibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Transparent)
                    )
                )
        )

        // --- BACK BUTTON ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 48.dp, start = 20.dp)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                .size(44.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        // --- CONTENT SHEET ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 340.dp) // Overlaps the image
                .shadow(24.dp, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            // Category Badge
            Surface(
                color = Color(0xFFE6FFFA),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = need.category.uppercase(),
                    color = TealMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = need.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B),
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(24.dp))

            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Funds Raised", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text("₹${need.fundsRaised}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TealMain)
                    }
                    Box(modifier = Modifier.size(1.dp, 40.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Goal Amount", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text("₹${need.targetCost}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("The Project", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Spacer(Modifier.height(12.dp))
            Text(
                text = need.description,
                fontSize = 15.sp,
                color = Color(0xFF475569),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(40.dp))

            // --- ACTION AREA ---
            if (isMember) {
                Text("Make a Pledge", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) customAmount = it },
                    placeholder = { Text("Enter amount in ₹", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Payments, null, tint = TealMain) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealMain,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val amount = customAmount.toIntOrNull() ?: 0
                        if (amount > 0) {
                            scope.launch {
                                isPledging = true
                                try {
                                    val newTotal = need.fundsRaised + amount
                                    db.collection("needs").document(need.id).update("fundsRaised", newTotal).await()
                                    Toast.makeText(context, "Successfully Pledged ₹$amount!", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                                isPledging = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealMain),
                    enabled = !isPledging
                ) {
                    if (isPledging) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Send Support", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                Surface(
                    color = Color(0xFFFFEBEB),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, null, tint = Color.Red)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Become a member in your profile to enable support for this school.",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}
