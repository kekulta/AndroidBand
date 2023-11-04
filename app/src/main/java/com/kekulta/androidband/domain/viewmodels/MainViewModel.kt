package com.kekulta.androidband.domain.viewmodels

import com.kekulta.androidband.domain.audio.capture.CaptureRepository
import com.kekulta.androidband.presentation.framework.PermissionManager
import com.kekulta.androidband.presentation.framework.PermissionRequest
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    private val captureRepository: CaptureRepository,
    private val permissionManager: PermissionManager,
) : CoroutineViewModel() {

    fun observePermissionRequests(): Flow<PermissionRequest> {
        return permissionManager.permissionRequests
    }

    fun onPermissionRequestResult(requestId: Int, granted: Boolean) {
        permissionManager.onPermissionResult(requestId, granted)
    }

    fun onStartCapturing() {
        captureRepository.startCapturing()
    }
}