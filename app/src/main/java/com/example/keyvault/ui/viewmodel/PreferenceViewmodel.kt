package com.example.keyvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.keyvault.core.preferances.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferenceViewmodel @Inject constructor(private val preferenceManager: PreferenceManager) :
    ViewModel() {

    fun saveData(key: String, data: String) {
        preferenceManager.saveData(key, data)
    }

    fun getData(key: String): String? {
        return preferenceManager.getData(key)
    }

    fun clear() {
        preferenceManager.clear()
    }
}