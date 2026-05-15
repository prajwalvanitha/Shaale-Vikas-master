package com.example.shaalevikas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    email: String,
    contact: String,
    isMember: Boolean,
    onLogout: () -> Unit,
    onBecomeMemberClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val TealMain = Color(0xFF0D9488)
    val BgColor = Color(0xFFF8FAFC)

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- USER IDENTITY CARD ---
            Surface(
                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(TealMain),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userName.take(1).uppercase(), color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        if (isMember) {
                            Icon(
                                Icons.Default.Verified, null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(32.dp).align(Alignment.BottomEnd).background(Color.White, CircleShape).padding(2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(userName, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    Text(email, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- MEMBERSHIP STATUS BOX ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = if (isMember) Color(0xFFE6FFFA) else Color(0xFFFFEBEB),
                border = BorderStroke(1.dp, if (isMember) TealMain.copy(0.2f) else Color.Red.copy(0.2f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isMember) Icons.Default.VerifiedUser else Icons.Default.Info,
                        null,
                        tint = if (isMember) TealMain else Color.Red
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            if (isMember) "Verified Member" else "Membership Pending",
                            fontWeight = FontWeight.Bold, color = if (isMember) TealMain else Color.Red
                        )
                        Text(
                            if (isMember) "You have full pledging access." else "Complete profile to start helping.",
                            fontSize = 12.sp, color = Color.Gray
                        )
                    }
                }
            }

            if (!isMember) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onBecomeMemberClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealMain)
                ) {
                    Text("Unlock Member Access", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.weight(1f))

            // --- PREMIUM LOGOUT BAR ---
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red.copy(0.1f))
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(12.dp))
                Text("Logout Session", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }
    }
}
