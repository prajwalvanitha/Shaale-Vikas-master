package com.example.shaalevikas.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shaalevikas.ui.screens.*
import com.example.shaalevikas.ui.components.Need
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ShaaleVikasNavGraph() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // One instance shared across all screens
    val viewModel: DashboardViewModel = viewModel()

    var selectedNeed by remember { mutableStateOf<Need?>(null) }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // ✅ FIXED: Force refresh the user status on successful login
                    viewModel.refreshUserStatus()
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                isAdmin = auth.currentUser?.email == "prajwalbhat@shaalevikas.com",
                onAddNeedClick = { navController.navigate("add_need") },
                onNeedClick = { need ->
                    selectedNeed = need
                    navController.navigate("details")
                },
                onProfileClick = { navController.navigate("profile") },
                onChatClick = { navController.navigate("chat") },
                onSavedClick = { navController.navigate("saved_needs") },
                viewModel = viewModel
            )
        }

        composable("saved_needs") {
            SavedNeedsScreen(
                viewModel = viewModel,
                onNeedClick = { need ->
                    selectedNeed = need
                    navController.navigate("details")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("details") {
            selectedNeed?.let { need ->
                NeedDetailsScreen(
                    need = need,
                    isMember = viewModel.isMember,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable("profile") {
            ProfileScreen(
                userName = viewModel.userName,
                email = auth.currentUser?.email ?: "User Email",
                contact = "Verified Member",
                isMember = viewModel.isMember,
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo(0) }
                },
                onBecomeMemberClick = { navController.navigate("become_member") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("become_member") {
            BecomeMemberScreen(
                onSuccess = {
                    // Refresh status after registration
                    viewModel.refreshUserStatus()
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("chat") { ChatScreen(onBackClick = { navController.popBackStack() }) }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    viewModel.refreshUserStatus()
                    navController.popBackStack()
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onResetLinkSent = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("add_need") { AddNeedScreen(onBackClick = { navController.popBackStack() }) }
    }
}
