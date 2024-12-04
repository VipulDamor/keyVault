package com.example.keyvault.di

import android.content.Context
import com.example.keyvault.data.api.OAuthApi
import com.example.keyvault.data.repository.AuthRepositoryImpl
import com.example.keyvault.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OAuthModule {


    @Singleton
    @Provides
    fun provideAPiService(retrofit: Retrofit): OAuthApi {
        return retrofit.create(OAuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideProductRepository(productService: OAuthApi): AuthRepository {
        return AuthRepositoryImpl(productService)
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context
    ): AuthorizationService {
        return AuthorizationService(context)
    }


}
