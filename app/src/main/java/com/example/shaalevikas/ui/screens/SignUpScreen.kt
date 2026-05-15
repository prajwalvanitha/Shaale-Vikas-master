package com.example.shaalevikas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val TealMain = Color(0xFF0D9488)
    val GradientBackground = Brush.verticalGradient(listOf(TealMain, Color(0xFFF8FAFC), Color.White))

    Box(modifier = Modifier.fillMaxSize().background(GradientBackground)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // SV Logo Anchor
            Box(modifier = Modifier.size(80.dp).shadow(12.dp, CircleShape).background(Color.White, CircleShape).clip(CircleShape), contentAlignment = Alignment.Center) {
                Text("SV", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TealMain)
            }

            Spacer(Modifier.height(24.dp))
            Text("Create Account", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("Start your journey with Shaale Vikas", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(32.dp))

            // Glass Card
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), color = Color.White.copy(alpha = 0.9f), shadowElevation = 16.dp) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    ModernSignUpField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person, teal = TealMain)
                    Spacer(Modifier.height(12.dp))
                    ModernSignUpField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Default.AlternateEmail, teal = TealMain)
                    Spacer(Modifier.height(12.dp))
                    ModernSignUpField(value = password, onValueChange = { password = it }, label = "Set Password", icon = Icons.Default.Lock, isPassword = true, teal = TealMain)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val result = auth.createUserWithEmailAndPassword(email, password).await()
                                        val userProfile = hashMapOf("username" to name, "email" to email, "role" to "User", "isMember" to false)
                                        result.user?.uid?.let { db.collection("users").document(it).set(userProfile).await() }
                                        onSignUpSuccess()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                                    }
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(58.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealMain),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already a member? ", color = Color(0xFF475569))
                TextButton(onClick = onBackToLogin) { Text("Log In", color = TealMain, fontWeight = FontWeight.ExtraBold) }
            }
        }
    }
}

@Composable
fun ModernSignUpField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isPassword: Boolean = false, teal: Color) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(icon, null, tint = teal) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = teal,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF1F5F9),
            focusedContainerColor = Color(0xFFF1F5F9)
        )
    )
}
