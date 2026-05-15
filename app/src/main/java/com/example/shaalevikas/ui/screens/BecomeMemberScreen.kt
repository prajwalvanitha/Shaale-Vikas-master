package com.example.shaalevikas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeMemberScreen(onSuccess: () -> Unit, onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val TealMain = Color(0xFF0D9488)

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("Community Registration", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            // Instructional Card
            Surface(
                color = TealMain.copy(0.05f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Complete your profile to unlock pledging and support schools directly.",
                    modifier = Modifier.padding(16.dp),
                    color = TealMain,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            // --- PERSONAL SECTION ---
            SectionTitle("Personal Details")
            ModernField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Badge)
            Spacer(Modifier.height(12.dp))
            ModernField(value = contact, onValueChange = { contact = it }, label = "Phone Number")

            Spacer(Modifier.height(32.dp))

            // --- BANK SECTION ---
            SectionTitle("Bank Details (For Verification)")
            ModernField(value = bankName, onValueChange = { bankName = it }, label = "Bank Name", icon = Icons.Default.AccountBalance)
            Spacer(Modifier.height(12.dp))
            ModernField(value = accNumber, onValueChange = { accNumber = it }, label = "Account Number")
            Spacer(Modifier.height(12.dp))
            ModernField(value = ifsc, onValueChange = { ifsc = it }, label = "IFSC Code")

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && accNumber.isNotEmpty()) {
                        scope.launch {
                            isSaving = true
                            userId?.let { uid ->
                                try {
                                    val data = hashMapOf("isMember" to true, "fullName" to name, "bank" to bankName)
                                    db.collection("users").document(uid).set(data, SetOptions.merge()).await()
                                    Toast.makeText(context, "Welcome to the community!", Toast.LENGTH_LONG).show()
                                    onSuccess()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error saving profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealMain),
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Complete Profile", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun ModernField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = if (icon != null) { { Icon(icon, null, tint = Color(0xFF0D9488)) } } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D9488),
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}
