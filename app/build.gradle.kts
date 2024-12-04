plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)


    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.ksp)

}

android {
    namespace = "com.example.keyvault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.keyvault"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders.putIfAbsent("appAuthRedirectScheme", "guardianapp://oauth-callback")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }
    hilt {
        enableAggregatingTask = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.biometric)
    implementation(libs.androidx.security.crypto)
    implementation(libs.appauth)
    implementation(libs.accompanist.webview)

    //Compose
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.icons)
    implementation(libs.androidx.hilt.navigation)

    //Coroutines
    implementation(libs.kotlinx.coroutine.core)
    implementation(libs.kotlinx.coroutine.android)

    //Dagger-hilt
    implementation(libs.com.google.dagger)
    implementation(libs.core.ktx)
    ksp(libs.com.google.dagger.compiler)
    ksp(libs.androidx.hilt.hilt.compiler)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation (libs.okhttp.logging.interceptor)

    //test libs
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation (libs.kotlinx.coroutines.test)

    // Mockito
    testImplementation(libs.mockito.core)
    //testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
}