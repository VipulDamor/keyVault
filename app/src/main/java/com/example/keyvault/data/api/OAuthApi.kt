package com.example.keyvault.data.api

import com.example.keyvault.data.model.AuthResult
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OAuthApi {

    @FormUrlEncoded
    @POST("connect/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("scope") scope: String
    ): Response<AuthResult>

}