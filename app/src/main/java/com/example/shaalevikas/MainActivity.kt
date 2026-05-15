package com.example.shaalevikas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shaalevikas.ui.navigation.ShaaleVikasNavGraph
import com.example.shaalevikas.ui.screens.DashboardScreen
import com.example.shaalevikas.ui.theme.ShaaleVikasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This makes the app go full-screen behind the status bar
        enableEdgeToEdge()

        setContent {
            ShaaleVikasTheme {
                // This is the new entry point that handles all screens
                ShaaleVikasNavGraph()
            }
        }
    }
}
