package com.example.keyvault.core.navigation_utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.keyvault.ui.screens.FingerPrintScreen
import com.example.keyvault.ui.screens.HomeScreen
import com.example.keyvault.ui.screens.LoginScreen
import com.example.keyvault.ui.screens.PinScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navHostController = navController)
        }
        composable(Screen.FingerPrintScreen.route) {
            FingerPrintScreen(navHostController = navController)
        }
        composable(Screen.PinScreen.route) {
            PinScreen(navHostController = navController)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navHostController = navController)
        }
    }


}
fun NavHostController.navigateWithClearStack(route : String){
    val navController = this
    navController.navigate(route) {->
        navController.graph.startDestinationId.let {
            popUpTo(it) {
                inclusive = true
            }
        }
        launchSingleTop = true
    }
}