@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.keyvault.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.keyvault.R
import com.example.keyvault.core.Constant.Key.BIO_METRIC_REGISTERED
import com.example.keyvault.core.Constant.Key.DECLINED
import com.example.keyvault.core.Constant.Key.ENABLED
import com.example.keyvault.core.navigation_utils.Screen
import com.example.keyvault.core.navigation_utils.navigateWithClearStack
import com.example.keyvault.ui.component.MessageDialog
import com.example.keyvault.ui.viewmodel.BioMetricAuthViewmodel
import com.example.keyvault.ui.viewmodel.PreferenceViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FingerPrintScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController?,
    viewmodel: BioMetricAuthViewmodel = hiltViewModel<BioMetricAuthViewmodel>(),
    preferenceViewmodel: PreferenceViewmodel = hiltViewModel<PreferenceViewmodel>()
) {


    val status by viewmodel.status.collectAsState()
    val fragmentContext = LocalContext.current as FragmentActivity
    val coroutineScope = rememberCoroutineScope()


    val bioMetricRegistered = preferenceViewmodel.getData(BIO_METRIC_REGISTERED)
    val showBioMetricCancellationDialog = viewmodel.showCancellationDialog.collectAsStateWithLifecycle()
    val showBioMetricRegistrationDialog = viewmodel.showRegisterDialog.collectAsStateWithLifecycle()


    LaunchedEffect(bioMetricRegistered) {
        if (bioMetricRegistered == DECLINED) {
            navHostController?.navigateWithClearStack(Screen.PinScreen.route)
        }

        if (bioMetricRegistered == ENABLED) {
            viewmodel.authenticateBiometric(
                context = fragmentContext,
                onSuccess = {
                    navHostController?.navigateWithClearStack(Screen.HomeScreen.route)
                },
                onError = { error ->
                    viewmodel.setStatus("Authentication failed: $error  Login Using Pin ")
                    coroutineScope.launch {
                        delay(3000)
                        navHostController?.navigateWithClearStack(Screen.PinScreen.route)
                    }
                }
            )
        }

    }

    if (showBioMetricCancellationDialog.value) {
        ShowCancellationDialog(navHostController, viewmodel, preferenceViewmodel)
    }

    if (showBioMetricRegistrationDialog.value) {
        if (bioMetricRegistered.isNullOrEmpty()) {
            ShowBioMetricPreRegisterDialog(
                navHostController,
                viewmodel,
                fragmentContext,
                coroutineScope,
                preferenceViewmodel
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = { Text(text = stringResource(R.string.finger_print_screen)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = status, style = typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (!showBioMetricRegistrationDialog.value) {
                    viewmodel.setRegisterDialogStatus(true)
                }
            }) {
                Text(stringResource(R.string.authenticate_finger_print))
            }
        }
    }
}

@Composable
fun ShowBioMetricPreRegisterDialog(
    navHostController: NavHostController?,
    viewmodel: BioMetricAuthViewmodel,
    fragmentContext: FragmentActivity,
    coroutineScope: CoroutineScope,
    preferenceViewmodel: PreferenceViewmodel
) {

    MessageDialog(
        title = stringResource(R.string.secure_your_app_with_biometrics),
        message = stringResource(R.string.enable_biometric_authentication_to_enhance_your_app_s_security_using_your_fingerprint_or_face_unlock_ensures_quick_access_while_keeping_your_data_safe_and_protected),
        onConfirm = {
            viewmodel.setRegisterDialogStatus(false)
            viewmodel.authenticateBiometric(
                context = fragmentContext,
                onSuccess = {
                    coroutineScope.launch {
                        viewmodel.setStatus(fragmentContext.getString(R.string.biometrics_registered_successfully))
                        preferenceViewmodel.saveData(BIO_METRIC_REGISTERED, ENABLED)
                        delay(500)
                        navHostController?.navigateWithClearStack(Screen.PinScreen.route)
                    }
                },
                onError = { error ->
                    coroutineScope.launch {
                        viewmodel.setStatus("Authentication failed: $error  continue with pin setup")
                        delay(3000)
                        navHostController?.navigateWithClearStack(Screen.PinScreen.route)
                    }
                }
            )
        },
        onCancel = {
            viewmodel.setRegisterDialogStatus(false)
            viewmodel.setCancellationDialogStatus(true)
        }
    )
}

@Composable
private fun ShowCancellationDialog(
    navHostController: NavHostController?,
    viewmodel: BioMetricAuthViewmodel,
    preferenceViewmodel: PreferenceViewmodel
) {

    val showCancellationDialog = viewmodel.showCancellationDialog.collectAsStateWithLifecycle()

    if (showCancellationDialog.value) {

        MessageDialog(
            title = stringResource(R.string.enable_biometric_authentication),
            message = stringResource(R.string.are_you_sure_you_don_t_want_to_set_up_biometric_authentication_enabling_it_enhances_security_and_provides_quick_access_to_your_account),
            onConfirm = {
                viewmodel.setCancellationDialogStatus(false)
                preferenceViewmodel.saveData(BIO_METRIC_REGISTERED, DECLINED)
                navHostController?.navigateWithClearStack(Screen.PinScreen.route)
            },
            onCancel = {
                viewmodel.setCancellationDialogStatus(false)
            }
        )
    }
}
