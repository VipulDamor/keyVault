package com.example.keyvault.core

import android.content.Intent
import com.example.keyvault.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow


object Constant {
    const val BASEURL = "https://id.stage.kidsxap.com.au/"
    const val AUTHORIZE_URL = BASEURL + "connect/authorize"
    const val TOKEN_URL = BASEURL + "connect/token"
    const val CLIENT_ID = "GuardianApp"
    const val REDIRECT_URL = "${BuildConfig.APPLICATION_ID}://oauth-callback"
    const val SCOPE = "kidsxap XapFinanceApi"
    const val GRANT_TYPE = "client_credentials"


    object Key {
        const val OAUTH_LOGIN = "oauth_login"
        const val PIN_AUTH = "pin_auth"
        const val REGISTERED_PIN = "registered_pin"
        const val ENABLED = "enabled"
        const val DECLINED = "declined"
        const val BIO_METRIC_LOGIN = "biometric_auth"
        const val BIO_METRIC_REGISTERED = "biometric_registered"
    }

    object Data {
        val authResult = MutableStateFlow<Intent?>(null)
    }
}

