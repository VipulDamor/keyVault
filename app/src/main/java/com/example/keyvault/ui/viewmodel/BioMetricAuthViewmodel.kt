package com.example.keyvault.ui.viewmodel

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.keyvault.R
import com.example.keyvault.core.navigation_utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BioMetricAuthViewmodel @Inject constructor() : ViewModel() {

    private val _status = MutableStateFlow("Waiting for authentication...")
    val status: StateFlow<String> = _status

    private val _showCancellationDialog = MutableStateFlow(false)
    val showCancellationDialog: StateFlow<Boolean> = _showCancellationDialog

    private val _showRegisterDialog = MutableStateFlow(false)
    val showRegisterDialog: StateFlow<Boolean> = _showRegisterDialog

    fun setCancellationDialogStatus(status: Boolean) {
        _showCancellationDialog.update { status }
    }

    fun setRegisterDialogStatus(status: Boolean) {
        _showRegisterDialog.update { status }
    }

    fun setStatus(status: String) {
        _status.update { status }
    }


    fun authenticateBiometric(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isBiometricAvailable(context)) {
            onError(context.getString(R.string.your_device_does_not_support_biometric_authentication_or_no_biometric_data_is_configured_please_check_your_device_settings_to_enable_and_set_up_biometrics_for_enhanced_security))
            return
        }
        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_authentication))
            .setSubtitle(context.getString(R.string.authenticate_using_fingerprint_or_face))
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Device supports biometric and is configured
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // Device does not have biometric hardware
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Biometric hardware is currently unavailable
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Device supports biometric but no biometric is enrolled
                false
            }

            else -> false
        }
    }


}