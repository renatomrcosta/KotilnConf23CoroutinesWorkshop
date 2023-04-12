package com.kotlinconf.workshop.kettle.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kotlinconf.workshop.kettle.CelsiusTemperature
import com.kotlinconf.workshop.kettle.FahrenheitTemperature
import com.kotlinconf.workshop.kettle.KettlePowerState
import com.kotlinconf.workshop.kettle.network.KettleService
import com.kotlinconf.workshop.kettle.toFahrenheit
import com.kotlinconf.workshop.util.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class KettleViewModel(
    private val kettleService: KettleService,
    parentScope: CoroutineScope,
) {
    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> get() = _errorMessage
    private fun showErrorMessage(throwable: Throwable) {
        log("Error occurred: $throwable")
        _errorMessage.value = "Can't perform an operation: network error"
        scope.launch {
            delay(5000)
            _errorMessage.value = ""
        }
    }

    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable -> showErrorMessage(throwable) }

    private val scope = CoroutineScope(
        parentScope.coroutineContext
    )

    fun switchOn() {
        scope.launch {
            kettleService.switchOn()
        }
    }

    fun switchOff() {
        scope.launch {
            kettleService.switchOff()
        }
    }

    val kettlePowerState: Flow<KettlePowerState> =
        kettleService.observeKettlePowerState()

    val celsiusTemperature: Flow<CelsiusTemperature> =
        kettleService.observeTemperature()
            .shareIn(scope, SharingStarted.Lazily)

    val fahrenheitTemperature: Flow<FahrenheitTemperature?> =
        celsiusTemperature.map { it.toFahrenheit() }

    val smoothCelsiusTemperature: Flow<CelsiusTemperature?> =
        flowOf(null)
//        celsiusTemperature.map {
//            it.value
//        }.averageOfLast(5).map {
//            CelsiusTemperature(it)
//        }
}