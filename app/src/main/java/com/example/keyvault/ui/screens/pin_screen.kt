@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.keyvault.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.keyvault.R
import com.example.keyvault.core.Constant.Key.ENABLED
import com.example.keyvault.core.Constant.Key.PIN_AUTH
import com.example.keyvault.core.Constant.Key.REGISTERED_PIN
import com.example.keyvault.core.navigation_utils.Screen
import com.example.keyvault.core.navigation_utils.navigateWithClearStack
import com.example.keyvault.ui.component.MessageDialog
import com.example.keyvault.ui.component.PinEntryScreen
import com.example.keyvault.ui.component.StepMode
import com.example.keyvault.ui.viewmodel.PreferenceViewmodel

@Composable
fun PinScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController?,
    preferenceViewmodel: PreferenceViewmodel = hiltViewModel<PreferenceViewmodel>()
) {

    var errorCount by remember { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }


    if (showResetDialog) {
        MessageDialog(
            title = stringResource(R.string.too_many_failed_attempts),
            message = stringResource(R.string.you_have_entered_the_incorrect_pin_three_times_to_ensure_your_account_s_security_you_must_reset_your_pin_would_you_like_to_reset_it_now),
            onConfirm = {
                preferenceViewmodel.saveData(PIN_AUTH, "")
                preferenceViewmodel.saveData(REGISTERED_PIN, "")
                showResetDialog = false
            },
            onCancel = {
                showResetDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = { Text(text = "Secure Pin Screen") }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val pinCreated = preferenceViewmodel.getData(PIN_AUTH)
            if (pinCreated == ENABLED) {
                val registeredPin = preferenceViewmodel.getData(REGISTERED_PIN)
                PinEntryScreen(
                    stepMode = StepMode.Verify,
                    savedPin = registeredPin,
                    onPinVerified = { success ->
                        if (success.second) {
                            errorCount = 0
                            navHostController?.navigateWithClearStack(Screen.HomeScreen.route)
                        } else {
                            errorCount++
                            if (errorCount >= 3) {
                                showResetDialog = true
                            }
                        }
                    }
                )
            } else {
                PinEntryScreen(
                    stepMode = StepMode.Create,
                    onPinVerified = { success ->
                        if (success.second) {
                            preferenceViewmodel.saveData(PIN_AUTH, ENABLED)
                            preferenceViewmodel.saveData(
                                REGISTERED_PIN,
                                success.first.toString()
                            )
                            navHostController?.navigateWithClearStack(Screen.HomeScreen.route)
                        }
                    }
                )
            }
        }
    }
}