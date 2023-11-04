package com.kekulta.androidband.presentation.framework

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

typealias RationaleCallback = ((permissionRequest: () -> Unit) -> Unit)

class PermissionManager(val context: Context) : AbstractCoroutineManager() {
    private val requestsHolder = mutableMapOf<Int, PermissionRequest>()

    private val _permissionRequests = Channel<PermissionRequest>()
    val permissionRequests = _permissionRequests.receiveAsFlow()

    fun onPermissionResult(requestCode: Int, granted: Boolean) {
        requestsHolder[requestCode]?.resultCallback?.invoke(granted)
        requestsHolder.remove(requestCode)
    }

    fun requestPermission(request: PermissionRequest) {
        if (checkPermission(request.permission)) {
            request.resultCallback(true)
        } else {
            requestsHolder[request.requestCode] = request
            launchScope(request.requestCode) {
                _permissionRequests.send(request)
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}