package com.example.keyvault.data.repository

import com.example.keyvault.core.ApiResponse
import com.example.keyvault.data.api.OAuthApi
import com.example.keyvault.data.model.AuthResult
import com.example.keyvault.domain.repository.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(private val api: OAuthApi) : AuthRepository {

    override suspend fun getAccessToken(
        grantType: String,
        code: String,
        clientId: String,
        redirectUri: String,
        scope: String
    ): ApiResponse<AuthResult> {
        return try {
            val response = api.getAccessToken(grantType, code, clientId, redirectUri,scope)
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(response.message() + response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.localizedMessage ?: "Error in response")
        }
    }
}
