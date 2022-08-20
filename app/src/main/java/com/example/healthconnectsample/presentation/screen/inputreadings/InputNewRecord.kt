package com.example.healthconnectsample.presentation.screen.inputreadings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthconnectsample.R

@Composable
fun InputNewRecord(onInsertClick: (Double) -> Unit = {}) {
    var weightInput by remember { mutableStateOf("") }

    // Check if the input value is a valid weight
    fun hasValidDoubleInRange(weight: String): Boolean {
        val tempVal = weight.toDoubleOrNull()
        return if (tempVal == null) {
            false
        } else tempVal <= 1000
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = weightInput,
            onValueChange = {
                weightInput = it
            },

            label = {
                Text(stringResource(id = R.string.weight_input))
            },
            isError = !hasValidDoubleInRange(weightInput),
            keyboardActions = KeyboardActions { !hasValidDoubleInRange(weightInput) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (!hasValidDoubleInRange(weightInput)) {
            Text(
                text = stringResource(id = R.string.valid_weight_error_message),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            enabled = hasValidDoubleInRange(weightInput),
            onClick = {
                onInsertClick(weightInput.toDouble())
                // clear TextField when new weight is entered
                weightInput = ""
            },

            ) {
            Text(text = stringResource(id = R.string.add_readings_button))
        }

    }

}

@Preview
@Composable
fun InputNewRecordPreview() {
    InputNewRecord()
}