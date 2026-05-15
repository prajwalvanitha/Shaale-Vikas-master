package com.example.shaalevikas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shaalevikas.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onBackClick: () -> Unit) {
    val messages = remember { 
        mutableStateListOf(ChatMessage("Hello! I'm your Shaale Vikas assistant. How can I help you today?", false)) 
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }

    // Initialize Gemini AI
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    val TealMain = Color(0xFF0D9488)

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        messages.add(ChatMessage(text, true))
        val currentInput = text
        userInput = ""
        
        scope.launch {
            isTyping = true
            listState.animateScrollToItem(messages.size - 1)
            
            try {
                val response = generativeModel.generateContent(
                    content {
                        text("You are a helpful assistant for Shaale Vikas, an NGO platform for school infrastructure. " +
                             "Answer the following user query: $currentInput")
                    }
                )
                messages.add(ChatMessage(response.text ?: "I'm sorry, I couldn't process that.", false))
            } catch (e: Exception) {
                messages.add(ChatMessage("Error: Check your API key in local.properties", false))
            } finally {
                isTyping = false
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(TealMain.copy(0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, null, tint = TealMain, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Shaale Vikas AI", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Online Assistant", fontSize = 11.sp, color = TealMain, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8FAFC))) {

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(16.dp)) }
                items(messages) { msg -> ChatBubble(msg, TealMain) }

                if (isTyping) {
                    item { Text("AI is thinking...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp)) }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Chat Input Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).navigationBarsPadding().imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask anything...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { sendMessage(userInput) },
                        enabled = userInput.isNotBlank() && !isTyping,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = TealMain)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, teal: Color) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) teal else Color.White
    val txtColor = if (message.isUser) Color.White else Color(0xFF1E293B)

    val shape = if (message.isUser)
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = if (message.isUser) 4.dp else 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(16.dp),
                color = txtColor,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
