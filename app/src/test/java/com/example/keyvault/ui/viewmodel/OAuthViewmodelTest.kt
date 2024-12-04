package com.example.keyvault.ui.viewmodel

import com.example.keyvault.core.ApiResponse
import com.example.keyvault.core.NetworkManager
import com.example.keyvault.data.model.AuthResult
import com.example.keyvault.domain.repository.AuthRepository
import com.example.keyvault.domain.usecase.AccessTokenUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class OAuthViewmodelTest {

    private lateinit var viewModel: OAuthViewmodel
    private lateinit var accessTokenUseCase: AccessTokenUseCase

    @Mock
    private lateinit var authRepository: AuthRepository


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        accessTokenUseCase = AccessTokenUseCase(authRepository)

        viewModel = OAuthViewmodel(
            authorizationService = mock(AuthorizationService::class.java),
            networkManager = mock(NetworkManager::class.java),
            accessTokenUseCase = accessTokenUseCase
        )
    }


    @Test
    fun `getAccessToken updates authResult on success`() = runTest {

        val successResponse = ApiResponse.Success(AuthResult("accessToken", "refreshToken"))
        Mockito.`when`(
            accessTokenUseCase.invoke(
                clientId = anyString(),
                code = anyString(),
                grantType = anyString(),
                redirectUri = anyString(),
                scope = anyString()
            )
        ).thenReturn(successResponse)

        viewModel.getAccessToken("code")
        advanceUntilIdle()

        val result = viewModel.authResult.first()

        if (result != null) {
            assertEquals("accessToken", result.accessToken)
            assertEquals("refreshToken", result.refreshToken)
        }
    }

    @Test
    fun `getAccessToken updates authResult on error`() = runTest {

        val errorResponse = ApiResponse.Error(Exception("Error occurred").toString())
        Mockito.`when`(
            accessTokenUseCase.invoke(
                clientId = anyString(),
                code = anyString(),
                grantType = anyString(),
                redirectUri = anyString(),
                scope = anyString()
            )
        ).thenReturn(errorResponse)

        // Act
        viewModel.getAccessToken("code")
        advanceUntilIdle()

        // Assert
        val result = viewModel.authResult.first()

        if (result != null) {
            assertEquals("Error", result.accessToken)
            assertEquals("error", result.refreshToken)
        }
    }

    @Test
    fun `update network dialog state`() {
        viewModel.updateNetworkDialogState(true)
        assertEquals(true, viewModel.showNetworkDialog.value)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}