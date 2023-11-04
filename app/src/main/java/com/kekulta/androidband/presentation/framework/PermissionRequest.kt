package com.kekulta.androidband.presentation.framework

data class PermissionRequest(
    val permission: String,
    val requestCode: Int,
    val forceRationale: Boolean = false,
    val rationaleCallback: RationaleCallback?,
    val resultCallback: (granted: Boolean) -> Unit
)