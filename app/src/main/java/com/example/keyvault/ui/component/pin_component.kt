package com.example.keyvault.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.keyvault.ui.viewmodel.PinEntryViewModel


@Composable
fun PinEntryScreen(
    stepMode: StepMode,
    onPinVerified: (Pair<String?, Boolean>) -> Unit,
    savedPin: String? = null,
    viewModel: PinEntryViewModel = hiltViewModel<PinEntryViewModel>()
) {
    val currentPin by viewModel.currentPin.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val step by viewModel.step.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (stepMode) {
                StepMode.Create -> when (step) {
                    Step.EnterPin -> "Enter your PIN"
                    Step.ConfirmPin -> "Confirm your PIN"
                }

                StepMode.Verify -> "Enter your PIN to unlock"
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        PinDots(pin = currentPin)

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        NumberPad { input -> viewModel.onNumberInput(input) }

        if (currentPin.length == 6) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onNextClicked(stepMode, savedPin, onPinVerified) }
            ) {
                Text(text = if (stepMode == StepMode.Create && step == Step.EnterPin) "Next" else "Confirm")
            }
        }
    }
}
/*fun PinEntryScreen(
    stepMode: StepMode,
    onPinVerified: (Pair<String?, Boolean>) -> Unit,
    savedPin: String? = null
) {

    var currentPin by remember { mutableStateOf("") }
    var firstPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(Step.EnterPin) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (stepMode) {
                StepMode.Create -> when (step) {
                    Step.EnterPin -> "Enter your PIN"
                    Step.ConfirmPin -> "Confirm your PIN"
                }

                StepMode.Verify -> "Enter your PIN to unlock"
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        PinDots(pin = currentPin)

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        NumberPad { input ->
            when {
                input == "←" -> {
                    if (currentPin.isNotEmpty()) {
                        currentPin = currentPin.dropLast(1)
                    }
                }

                currentPin.length < 6 -> {
                    currentPin += input
                }
            }
        }

        if (currentPin.length == 6) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    when (stepMode) {
                        StepMode.Create -> {
                            when (step) {
                                Step.EnterPin -> {
                                    firstPin = currentPin
                                    currentPin = ""
                                    step = Step.ConfirmPin
                                }

                                Step.ConfirmPin -> {
                                    if (currentPin == firstPin) {

                                        onPinVerified(
                                            Pair(
                                                currentPin,
                                                true
                                            )
                                        ) // PIN created successfully
                                    } else {
                                        errorMessage = "PINs do not match. Please try again."
                                        currentPin = ""
                                        firstPin = ""
                                        step = Step.EnterPin
                                    }
                                }
                            }
                        }
                        StepMode.Verify -> {
                            if (currentPin == savedPin) {
                                onPinVerified(Pair(null, true)) // PIN verified successfully
                            } else {
                                onPinVerified(Pair(null, false))
                                errorMessage = "Incorrect PIN. Please try again."
                                currentPin = ""
                            }
                        }
                    }
                }
            ) {
                Text(text = if (stepMode == StepMode.Create && step == Step.EnterPin) "Next" else "Confirm")
            }
        }
    }
}*/

@Composable
fun PinDots(pin: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < pin.length) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun NumberPad(onInput: (String) -> Unit) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "←")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        numbers.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { number ->
                    NumberKey(number, onClick = {
                        onInput(number)
                    })
                }
            }
        }
    }
}

@Composable
fun NumberKey(number: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.bodyLarge,
            color = if (number == "←") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}


enum class Step {
    EnterPin,
    ConfirmPin
}

enum class StepMode {
    Create,
    Verify
}


