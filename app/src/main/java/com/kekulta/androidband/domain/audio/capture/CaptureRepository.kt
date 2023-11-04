package com.kekulta.androidband.domain.audio.capture

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CaptureRepository {
    private val _isCapturing = MutableStateFlow(false)

    fun observeCapturing(): StateFlow<Boolean> = _isCapturing

    fun startCapturing() {
        _isCapturing.value = true
    }

    fun endCapturing() {
        _isCapturing.value = false
    }
}