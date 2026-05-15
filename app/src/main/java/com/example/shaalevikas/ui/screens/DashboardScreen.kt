package com.example.shaalevikas.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shaalevikas.ui.components.Need
import com.example.shaalevikas.ui.components.NeedCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddNeedClick: () -> Unit = {},
    onNeedClick: (Need) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    isAdmin: Boolean,
    viewModel: DashboardViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var showNotifications by remember { mutableStateOf(false) }

    val tealMain = Color(0xFF0D9488)
    val bgColor = Color(0xFFF8FAFC)

    Scaffold(
        containerColor = bgColor,
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddNeedClick,
                    containerColor = tealMain,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.shadow(12.dp, RoundedCornerShape(20.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Need", modifier = Modifier.size(30.dp))
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 24.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShaaleBottomNav(Icons.Default.Home, "Home", true, tealMain, {})
                    ShaaleBottomNav(Icons.Outlined.FavoriteBorder, "Saved", false, tealMain, onSavedClick)
                    Spacer(modifier = Modifier.width(48.dp))
                    ShaaleBottomNav(Icons.Outlined.Email, "Chat", false, tealMain, onChatClick)
                    ShaaleBottomNav(Icons.Outlined.Person, "Profile", false, tealMain, onProfileClick)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            HeaderSection(name = viewModel.userName, onNotificationClick = { showNotifications = true })

            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search schools...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = { Icon(Icons.Default.Search, tint = tealMain, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tealMain,
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            SmartTipBanner(onStartClick = { scope.launch { scrollState.animateScrollTo(1500) } })

            VisionBox()

            Spacer(modifier = Modifier.height(32.dp))

            Text("Categories", modifier = Modifier.padding(horizontal = 24.dp), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))

            CategoryRow(
                selectedCategory = viewModel.selectedCategory,
                onCategorySelected = { viewModel.selectedCategory = it },
                teal = tealMain
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (viewModel.selectedCategory == "All") "Trending Schools" else "${viewModel.selectedCategory} Needs",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 16.dp, start = 24.dp, bottom = 32.dp)
            ) {
                val filteredNeeds = viewModel.needsList.filter {
                    (viewModel.selectedCategory == "All" || it.category == viewModel.selectedCategory) &&
                            it.title.contains(searchQuery, ignoreCase = true)
                }

                if (filteredNeeds.isEmpty()) {
                    Text("No projects found", color = Color.Gray, modifier = Modifier.padding(16.dp))
                } else {
                    filteredNeeds.forEach { needItem ->
                        NeedCard(
                            need = needItem,
                            isSaved = viewModel.savedNeedsIds.contains(needItem.id),
                            onSaveToggle = { viewModel.toggleSaveNeed(needItem.id) },
                            isAdmin = isAdmin,
                            modifier = Modifier.clickable { onNeedClick(needItem) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showNotifications) {
            ModalBottomSheet(
                onDismissRequest = { showNotifications = false },
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle(color = tealMain) }
            ) {
                NotificationContent(isAdmin, viewModel.isMember)
            }
        }
    }
}

// --- SUPPORT COMPONENTS ---

@Composable
fun VisionBox() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = Color(0xFF0D9488).copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
            Text(
                text = "Every child deserves a desk, and every school deserves a roof. We bridge the gap between your generosity and students in need.",
                fontSize = 15.sp,
                color = Color(0xFF475569),
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun HeaderSection(name: String, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Welcome back,", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text("$name!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
        }
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .size(52.dp)
                .shadow(4.dp, CircleShape)
        ) {
            // FIXED: Added contentDescription = null
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = Color(0xFF0D9488),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SmartTipBanner(onStartClick: () -> Unit) {
    val bannerGradient = Brush.horizontalGradient(listOf(Color(0xFF134E4A), Color(0xFF0D9488)))
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).shadow(12.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Box(modifier = Modifier.background(bannerGradient).padding(28.dp)) {
            Column {
                Surface(color = Color.White.copy(alpha = 0.2f), shape = CircleShape) {
                    Text(" SMART TIP ", color = Color(0xFF5EEAD4), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text("Change the world\nwith a small help", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Pledging", color = Color(0xFF0D9488), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun CategoryRow(selectedCategory: String, onCategorySelected: (String) -> Unit, teal: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryItem(Icons.Default.GridView, "All", selectedCategory == "All", teal) { onCategorySelected("All") }
        CategoryItem(Icons.Default.MedicalServices, "Medical", selectedCategory == "Medical", teal) { onCategorySelected("Medical") }
        CategoryItem(Icons.Default.School, "Education", selectedCategory == "Education", teal) { onCategorySelected("Education") }
        CategoryItem(Icons.Default.Coronavirus, "Pandemic", selectedCategory == "Pandemic", teal) { onCategorySelected("Pandemic") }
        CategoryItem(Icons.Default.SportsBasketball, "Sports", selectedCategory == "Sports", teal) { onCategorySelected("Sports") }
    }
}

@Composable
fun CategoryItem(icon: ImageVector, label: String, isActive: Boolean, activeColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(if (isActive) activeColor else Color.White).border(1.dp, if (isActive) Color.Transparent else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isActive) Color.White else Color(0xFF94A3B8), modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 13.sp, color = if (isActive) activeColor else Color(0xFF64748B), fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun NotificationContent(isAdmin: Boolean, isMember: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 48.dp)) {
        Text("Activity Center", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
        Spacer(Modifier.height(24.dp))
        NotificationItem("System Live", "Authentication servers are active.", Icons.Default.CloudDone)
        if (isMember) NotificationItem("Member Verified", "Your pledge tools are unlocked.", Icons.Default.Verified)
        if (isAdmin) NotificationItem("Admin Portal", "Project management tools active.", Icons.Default.AdminPanelSettings)
    }
}

@Composable
fun NotificationItem(title: String, desc: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).background(Color(0xFFE6FFFA), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            Text(desc, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ShaaleBottomNav(icon: ImageVector, label: String, isActive: Boolean, activeColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Icon(imageVector = icon, contentDescription = null, tint = if (isActive) activeColor else Color(0xFF94A3B8), modifier = Modifier.size(26.dp))
        Text(text = label, fontSize = 11.sp, color = if (isActive) activeColor else Color(0xFF94A3B8), fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium)
    }
}
