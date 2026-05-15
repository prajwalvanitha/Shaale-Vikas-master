package com.example.shaalevikas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNeedScreen(onBackClick: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetCost by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Education") }
    var imageUrl by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    val TealMain = Color(0xFF0D9488)

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("Launch New Project", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            // Visual Header
            Surface(color = TealMain.copy(0.1f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PostAdd, null, tint = TealMain)
                    Spacer(Modifier.width(12.dp))
                    Text("Enter project details to list them on the trending dashboard.", color = TealMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- FORM FIELDS ---
            SectionLabel("Basic Information")
            ModernEntryField(value = title, onValueChange = { title = it }, label = "Project Title (e.g. Science Lab)", icon = Icons.Default.Title)
            Spacer(Modifier.height(12.dp))
            ModernEntryField(value = category, onValueChange = { category = it }, label = "Category (Sports, Medical, etc.)", icon = Icons.Default.Category)

            Spacer(Modifier.height(24.dp))

            SectionLabel("Resources & Media")
            ModernEntryField(value = imageUrl, onValueChange = { imageUrl = it }, label = "Direct Image URL (i.ibb.co/...)", icon = Icons.Default.AddPhotoAlternate)
            Spacer(Modifier.height(12.dp))
            ModernEntryField(value = targetCost, onValueChange = { targetCost = it }, label = "Funding Goal (₹)", icon = Icons.Default.AccountBalanceWallet)

            Spacer(Modifier.height(24.dp))

            SectionLabel("The Story")
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                placeholder = { Text("Explain why this project needs support...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealMain,
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && imageUrl.isNotEmpty()) {
                        scope.launch {
                            isSaving = true
                            try {
                                val newNeed = hashMapOf(
                                    "title" to title, "description" to description, "imageUrl" to imageUrl,
                                    "targetCost" to (targetCost.toLongOrNull() ?: 0L), "fundsRaised" to 0L,
                                    "category" to category, "daysLeft" to 30, "isPriority" to false
                                )
                                db.collection("needs").add(newNeed).await()
                                Toast.makeText(context, "Project Launched!", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealMain),
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Publish Project", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp))
}

@Composable
fun ModernEntryField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(icon, null, tint = Color(0xFF0D9488)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D9488),
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}
