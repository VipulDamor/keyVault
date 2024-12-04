package com.example.keyvault.core.navigation_utils

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object FingerPrintScreen : Screen("finger_print_screen")
    data object PinScreen : Screen("pin_screen")
    data object HomeScreen : Screen("home_screen")
}