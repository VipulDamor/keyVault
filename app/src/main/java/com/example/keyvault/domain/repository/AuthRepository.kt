package com.example.keyvault.domain.repository

import com.example.keyvault.core.ApiResponse
import com.example.keyvault.data.model.AuthResult

interface AuthRepository {
    suspend fun getAccessToken(
        grantType: String,
        code: String,
        clientId: String,
        redirectUri: String,
        scope: String
    ): ApiResponse<AuthResult>
}