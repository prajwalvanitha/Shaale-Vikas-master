package com.example.shaalevikas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shaalevikas.ui.components.Need
import com.example.shaalevikas.ui.components.NeedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNeedsScreen(
    viewModel: DashboardViewModel,
    onNeedClick: (Need) -> Unit,
    onBackClick: () -> Unit
) {
    val savedItems = viewModel.needsList.filter { viewModel.savedNeedsIds.contains(it.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Needs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // FIXED: Used the non-deprecated AutoMirrored version
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (savedItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("You haven't saved any needs yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                items(savedItems) { needItem ->
                    NeedCard(
                        need = needItem,
                        isSaved = true,
                        onSaveToggle = { viewModel.toggleSaveNeed(needItem.id) },
                        // FIXED: Added the missing isAdmin parameter
                        isAdmin = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNeedClick(needItem) }
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
