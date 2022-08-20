package com.example.healthconnectsample.presentation.screen.inputreadings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.example.healthconnectsample.R
import com.example.healthconnectsample.data.dateTimeWithOffsetOrDefault
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun WeightElement(reading: WeightRecord, onDeleteClick: (String) -> Unit) {
    // show local date and time
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    val zonedDateTime =
        dateTimeWithOffsetOrDefault(reading.time, reading.zoneOffset)
    val uid = reading.metadata.uid
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${reading.weight}" + " ",
        )
        Text(text = formatter.format(zonedDateTime))
        IconButton(
            onClick = {
                if (uid != null) {
                    onDeleteClick(uid)
                }
            },
        ) {
            Icon(
                Icons.Default.Delete,
                stringResource(R.string.delete_button_readings),
            )
        }
    }
}

@Preview
@Composable
fun WeightElementPreview() {
    val inputTime = Instant.now()

    WeightElement(
        reading =
        WeightRecord(
            Mass.kilograms(54.0),
            time = inputTime,
            zoneOffset = null
        ),
        onDeleteClick = {}
    )
}