import android.content.Context
import org.mockito.Mockito.mock
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import com.example.keyvault.core.NetworkManagerImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NetworkManagerImplTest {


   private val context: Context = ApplicationProvider.getApplicationContext()

    // Returns true when WiFi connection is available
    @Test
    fun is_internet_available_returns_true_when_wifi_available() = runBlocking{
        // Arrange

        val connectivityManager = mock(ConnectivityManager::class.java)
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        val network = mock(Network::class.java)

        `when`(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)

        val networkManager = NetworkManagerImpl(context)

        // Act
        val result = networkManager.isInternetAvailable()

        // Assert
        assertTrue(result)
    }

    // Returns false when no active network is available
    @Test
    fun is_internet_available_returns_false_when_no_active_network() {
        // Arrange

        val connectivityManager = mock(ConnectivityManager::class.java)

        `when`(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(null)

        val networkManager = NetworkManagerImpl(context)

        // Act
        val result = networkManager.isInternetAvailable()

        // Assert
        assertFalse(result)
    }
}