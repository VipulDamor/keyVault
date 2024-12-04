package com.example.keyvault.ui.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keyvault.core.ApiResponse
import com.example.keyvault.core.Constant.CLIENT_ID
import com.example.keyvault.core.Constant.GRANT_TYPE
import com.example.keyvault.core.Constant.REDIRECT_URL
import com.example.keyvault.core.Constant.SCOPE
import com.example.keyvault.core.NetworkManager
import com.example.keyvault.data.model.AuthResult
import com.example.keyvault.domain.usecase.AccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

@HiltViewModel
class OAuthViewmodel @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val networkManager: NetworkManager,
    private val accessTokenUseCase: AccessTokenUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    private val _showNetworkDialog = MutableStateFlow(false)
    val showNetworkDialog: StateFlow<Boolean> = _showNetworkDialog

    init {
        updateNetworkDialogState(networkManager.isInternetAvailable())
    }

    fun getAccessToken(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = accessTokenUseCase.invoke(
                clientId = CLIENT_ID,
                code = code,
                grantType = GRANT_TYPE,
                redirectUri = REDIRECT_URL,
                scope = SCOPE

            )
            when (response) {
                is ApiResponse.Error -> {
                    _authResult.update { AuthResult(accessToken = "Error", refreshToken = "error") }
                }

                is ApiResponse.Success -> {
                    _authResult.update { response.data }
                }
            }
        }
    }

    fun updateNetworkDialogState(state: Boolean) {
        _showNetworkDialog.update { state }
    }

    fun authorize(
        serviceConfiguration: AuthorizationServiceConfiguration,
        clientId: String,
        redirectUri: Uri
    ): Intent {
        val authRequest = AuthorizationRequest.Builder(
            serviceConfiguration,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        ).setScope(SCOPE).setPrompt("login").build()

        return authorizationService.getAuthorizationRequestIntent(authRequest)
    }

    fun handleAuthorizationResponse(intent: Intent?) {
        val response = intent?.let { AuthorizationResponse.fromIntent(it) }
        val exception = intent?.let { AuthorizationException.fromIntent(it) }

        val code = intent?.data?.getQueryParameter("code")
        if (code != null) {
            _authState.value = AuthState.Authorized(code)
        } else {
            _authState.value = AuthState.Error(exception?.message ?: "Unknown error")
        }
    }

}

sealed class AuthState {
    data object Idle : AuthState()
    data class Authorized(val code: String) : AuthState()
    data class Error(val errorMessage: String) : AuthState()
}