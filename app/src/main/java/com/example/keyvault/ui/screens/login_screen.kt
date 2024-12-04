@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.keyvault.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.keyvault.R
import com.example.keyvault.core.Constant
import com.example.keyvault.core.Constant.AUTHORIZE_URL
import com.example.keyvault.core.Constant.CLIENT_ID
import com.example.keyvault.core.Constant.Key.DECLINED
import com.example.keyvault.core.Constant.Key.ENABLED
import com.example.keyvault.core.Constant.REDIRECT_URL
import com.example.keyvault.core.Constant.TOKEN_URL
import com.example.keyvault.core.navigation_utils.Screen
import com.example.keyvault.core.navigation_utils.navigateWithClearStack
import com.example.keyvault.ui.component.MessageDialog
import com.example.keyvault.ui.viewmodel.AuthState
import com.example.keyvault.ui.viewmodel.OAuthViewmodel
import com.example.keyvault.ui.viewmodel.PreferenceViewmodel
import kotlinx.coroutines.delay
import net.openid.appauth.AuthorizationServiceConfiguration

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController?,
    viewModel: OAuthViewmodel = hiltViewModel<OAuthViewmodel>(),
    preferenceViewmodel: PreferenceViewmodel = hiltViewModel<PreferenceViewmodel>()
) {


    val fragmentContext = LocalContext.current as FragmentActivity

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val authResult = Constant.Data.authResult.collectAsStateWithLifecycle().value
    val isNetworkAvailable = viewModel.showNetworkDialog.collectAsStateWithLifecycle().value
    val accessTokenResult = viewModel.authResult.collectAsStateWithLifecycle().value

    var oauthLoginEnabled = preferenceViewmodel.getData(Constant.Key.OAUTH_LOGIN)


    if (!isNetworkAvailable && oauthLoginEnabled != ENABLED) {
        ShowNoInternetDialog(viewModel)
    }

    LaunchedEffect(key1 = oauthLoginEnabled, key2 = accessTokenResult) {
        if (accessTokenResult != null) {
            preferenceViewmodel.saveData(Constant.Key.OAUTH_LOGIN, ENABLED)
            oauthLoginEnabled = preferenceViewmodel.getData(Constant.Key.OAUTH_LOGIN)
            delay(3000)
        }
        val fingerPrintEnrolled = preferenceViewmodel.getData(Constant.Key.BIO_METRIC_REGISTERED)

        if (oauthLoginEnabled == ENABLED) {
            if (fingerPrintEnrolled.isNullOrEmpty() || fingerPrintEnrolled == ENABLED) {
                navHostController?.navigateWithClearStack(Screen.FingerPrintScreen.route)
            } else if (fingerPrintEnrolled == DECLINED) {
                navHostController?.navigateWithClearStack(Screen.PinScreen.route)
            }
        }
    }

    LaunchedEffect(authResult) {
        if (authResult?.data != null) {
            viewModel.handleAuthorizationResponse(authResult)
        }
    }



    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(), title = {
                Text(text = "Login Screen")
            })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (authState) {
                is AuthState.Idle -> {
                    Button(onClick = {
                        val intent = viewModel.authorize(
                            serviceConfiguration = AuthorizationServiceConfiguration(
                                Uri.parse(AUTHORIZE_URL),
                                Uri.parse(TOKEN_URL)
                            ),
                            clientId = CLIENT_ID,
                            redirectUri = Uri.parse(REDIRECT_URL)
                        )
                        startActivityForResult(fragmentContext, intent, 202, null)
                    }) {
                        Text(stringResource(R.string.login_with_oauth))
                    }
                }

                is AuthState.Authorized -> {
                    Column(
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text(
                            "Authorization Successful: ${(authState as AuthState.Authorized).code}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    viewModel.getAccessToken(code = (authState as AuthState.Authorized).code)
                }

                is AuthState.Error -> {
                    Text(
                        "Authorization Failed: ${(authState as AuthState.Error).errorMessage}",
                        modifier = Modifier.padding(16.dp)
                    )
                }

            }
        }

    }
}

@Composable
fun ShowNoInternetDialog(viewModel: OAuthViewmodel) {
    MessageDialog(
        title = stringResource(R.string.no_internet_connection),
        message = stringResource(R.string.it_seems_you_re_not_connected_to_the_internet_please_check_your_connection_and_try_again),
        onConfirm = {
            viewModel.updateNetworkDialogState(true)
        },
        onCancel = {
            viewModel.updateNetworkDialogState(true)
        }
    )
}
