/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.healthconnectsample.presentation.screen.inputreadings

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthconnectsample.data.HealthConnectManager
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class InputReadingsViewModel(private val healthConnectManager: HealthConnectManager) :
    ViewModel() {
    val permissions = setOf(
        Permission.createReadPermission(WeightRecord::class),
        Permission.createWritePermission(WeightRecord::class),
    )
    var weeklyAvg: MutableState<Mass?> = mutableStateOf(Mass.kilograms(0.0))
        private set

    var permissionsGranted = mutableStateOf(false)
        private set

    var readingsList: MutableState<List<WeightRecord>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    var projectedWeight: MutableState<Double> = mutableStateOf(100.0)
        private set

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                readWeightInputs()
            }
        }
    }

    fun inputReadings(inputValue: Double) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val time = ZonedDateTime.now().withNano(0)
                val weight = WeightRecord(
                    weight = Mass.kilograms(inputValue),
                    time = time.toInstant(),
                    zoneOffset = time.offset
                )
                healthConnectManager.writeWeightInput(weight)
                readWeightInputs()
            }
        }
    }

    fun deleteWeightInput(uid: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.deleteWeightInput(uid)
                readWeightInputs()
            }
        }
    }

    private suspend fun readWeightInputs() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endofWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)
        readingsList.value = healthConnectManager.readWeightInputs(startOfDay.toInstant(), now)
        weeklyAvg.value =
            healthConnectManager.computeWeeklyAverage(startOfDay.toInstant(), endofWeek)
        calculateProjectedWeight()
    }

    private fun calculateProjectedWeight() {
        if (readingsList.value.size > 2) {
            projectedWeight.value = doLinearInterpolation(readingsList.value)
        } else {
            projectedWeight.value = 0.0
        }
    }

    private fun doLinearInterpolation(values: List<WeightRecord>): Double {
        val x = values.size

        val x1 = values.size - 2
        val x2 = values.size - 1

        val y1 = values[x1].weight.inKilograms
        val y2 = values[x2].weight.inKilograms

        val numerator = y2 - y1
        val denominator = x2 - x1

        // Formula
        // y = y1 + (x-x1) ((y2-y1) / (x2-x1))

        return y1 + (x - x1) * (numerator / denominator)

    }

    /**
     * Provides permission check and error handling for Health Connect suspend function calls.
     *
     * Permissions are checked prior to execution of [block], and if all permissions aren't granted
     * the [block] won't be executed, and [permissionsGranted] will be set to false, which will
     * result in the UI showing the permissions button.
     *
     * Where an error is caught, of the type Health Connect is known to throw, [uiState] is set to
     * [UiState.Error], which results in the snackbar being used to show the error message.
     */
    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        // A random UUID is used in each Error object to allow errors to be uniquely identified,
        // and recomposition won't result in multiple snackbars.
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

class InputReadingsViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputReadingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputReadingsViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
