import com.example.keyvault.core.ApiResponse
import com.example.keyvault.data.api.OAuthApi
import com.example.keyvault.data.repository.AuthRepositoryImpl
import com.example.keyvault.data.model.AuthResult
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    private val mockApi = mock(OAuthApi::class.java)
    private val repository = AuthRepositoryImpl(mockApi)

    @Before
    fun setUp() {

    }




    @Test
    fun get_access_token_success_returns_auth_result() = runBlocking {

        val authResult = AuthResult("access123", "refresh123")
        val response = Response.success(authResult)

        whenever(
            mockApi.getAccessToken(
                grantType = "authorization_code",
                code = "auth_code",
                clientId = "client_123",
                redirectUri = "https://example.com",
                scope = "read write"
            )
        ).thenReturn(response)

        // Act
        val result = repository.getAccessToken(
            grantType = "authorization_code",
            code = "auth_code",
            clientId = "client_123",
            redirectUri = "https://example.com",
            scope = "read write"
        )

        // Assert
        assertTrue(result is ApiResponse.Success)
        assertEquals(authResult, (result as ApiResponse.Success).data)
    }

    // Empty or invalid response body returns ApiResponse.Error
    @Test
    fun get_access_token_null_body_returns_error() = runBlocking {
        // Arrange

        val response = Response.success<AuthResult>(null)

        whenever(
            mockApi.getAccessToken(
                grantType = "authorization_code",
                code = "auth_code",
                clientId = "client_123",
                redirectUri = "https://example.com",
                scope = "read write"
            )
        ).thenReturn(response)


        // Act
        val result = repository.getAccessToken(
            grantType = "authorization_code",
            code = "auth_code",
            clientId = "client_123",
            redirectUri = "https://example.com",
            scope = "read write"
        )

        // Assert
        assertTrue(result is ApiResponse.Error)
        assertTrue((result as ApiResponse.Error).message.isNotEmpty())
    }
}