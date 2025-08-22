package com.lbs.background_android_service.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ServiceState(
    val isRunning: Boolean = false,
    val url: String? = null
)

object ServiceStatusHolder {
    private val _status = MutableStateFlow(ServiceState())
    val status = _status.asStateFlow()

    fun updateState(isRunning: Boolean, url: String?) {
        _status.value = ServiceState(isRunning, url)
    }
}
