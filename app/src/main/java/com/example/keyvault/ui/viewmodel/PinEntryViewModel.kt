package com.example.keyvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.keyvault.ui.component.Step
import com.example.keyvault.ui.component.StepMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PinEntryViewModel @Inject constructor() : ViewModel() {


    private val _currentPin = MutableStateFlow("")
    val currentPin: StateFlow<String> get() = _currentPin

    private val _firstPin = MutableStateFlow("")

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    private val _step = MutableStateFlow(Step.EnterPin)
    val step: StateFlow<Step> get() = _step

    fun updateFirstPin(firstPin: String) {
        _firstPin.update { firstPin }
    }

    fun onNumberInput(input: String) {
        if (input == "←") {
            if (_currentPin.value.isNotEmpty()) {
                _currentPin.value = _currentPin.value.dropLast(1)
            }
        } else if (_currentPin.value.length < 6) {
            _currentPin.value += input
        }
    }

    fun onNextClicked(
        stepMode: StepMode,
        savedPin: String?,
        onPinVerified: (Pair<String?, Boolean>) -> Unit
    ) {
        when (stepMode) {
            StepMode.Create -> handleCreateStep(onPinVerified)
            StepMode.Verify -> handleVerifyStep(savedPin, onPinVerified)
        }
    }

    private fun handleCreateStep(onPinVerified: (Pair<String?, Boolean>) -> Unit) {
        when (_step.value) {
            Step.EnterPin -> {
                updateFirstPin(_currentPin.value)
                _currentPin.value = ""
                _step.value = Step.ConfirmPin
            }

            Step.ConfirmPin -> {
                if (_currentPin.value == _firstPin.value) {
                    onPinVerified(Pair(_currentPin.value, true)) // PIN created successfully
                } else {
                    _errorMessage.value = "PINs do not match. Please try again."
                    resetForRetry()
                }
            }
        }
    }

    private fun handleVerifyStep(
        savedPin: String?,
        onPinVerified: (Pair<String?, Boolean>) -> Unit
    ) {
        if (_currentPin.value == savedPin) {
            onPinVerified(Pair(null, true))
        } else {
            _errorMessage.value = "Incorrect PIN. Please try again."
            _currentPin.value = ""
            onPinVerified(Pair(null, false))
        }
    }

    private fun resetForRetry() {
        _currentPin.value = ""
        _firstPin.value = ""
        _step.value = Step.EnterPin
    }
}