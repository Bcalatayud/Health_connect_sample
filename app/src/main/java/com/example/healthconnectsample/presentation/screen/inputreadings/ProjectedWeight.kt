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
import com.example.healthconnectsample.R

@Composable
fun ProjectedWeight(projectedWeight: State<Double>) {
    val pw by projectedWeight
    if (pw != 0.0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.projected_weight), fontSize = 24.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Text(text = "$pw " + stringResource(id = R.string.kilograms))
        }

    }
}

@Preview
@Composable
fun ProjectWeightPReview() {
    ProjectedWeight(projectedWeight = derivedStateOf { 100.0 })
}