package com.example.healthconnectsample.presentation.screen.inputreadings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.units.Mass
import com.example.healthconnectsample.R

@Composable
fun WeightAverage(weeklyAvgState: State<Mass?>) {
    val weeklyAvg by weeklyAvgState
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.weekly_avg), fontSize = 24.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        if (weeklyAvg == null) {
            Text(text = "0.0" + stringResource(id = R.string.kilograms))
        } else {
            Text(text = "$weeklyAvg".take(5) + stringResource(id = R.string.kilograms))
        }
    }

}

@Preview
@Composable
fun WeightAveragePreview() {
    WeightAverage(weeklyAvgState = derivedStateOf { Mass.kilograms(54.5) })
}