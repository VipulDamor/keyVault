package com.example.keyvault.domain.usecase

import com.example.keyvault.core.ApiResponse
import com.example.keyvault.data.model.AuthResult
import com.example.keyvault.domain.repository.AuthRepository
import javax.inject.Inject

class AccessTokenUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        grantType: String,
        code: String,
        clientId: String,
        redirectUri: String,
        scope: String
    ): ApiResponse<AuthResult> = repository.getAccessToken(grantType, code, clientId, redirectUri,scope)
}
