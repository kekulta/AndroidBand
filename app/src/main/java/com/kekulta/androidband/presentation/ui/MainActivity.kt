package com.kekulta.androidband.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kekulta.androidband.R
import com.kekulta.androidband.databinding.ActivityMainBinding
import com.kekulta.androidband.domain.viewmodels.MainViewModel
import com.kekulta.androidband.presentation.framework.AudioCaptureService
import com.kekulta.androidband.presentation.framework.PermissionRequest
import com.kekulta.androidband.snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            viewModel.observePermissionRequests().collect(::handlePermissionRequest)
        }
    }

    private fun handlePermissionRequest(request: PermissionRequest) {
        val shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this, request.permission)
        if ((shouldShowRationale || request.forceRationale) && request.rationaleCallback != null) {
            request.rationaleCallback.invoke {
                requestPermission(
                    request.requestCode, request.permission
                )
            }
        } else {
            requestPermission(request.requestCode, request.permission)
        }
    }

    private fun requestPermission(requestCode: Int, permission: String) {
        ActivityCompat.requestPermissions(
            this, arrayOf(permission), requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onPermissionRequestResult(
            requestCode, grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.root.snackbar("MediaProjection permission obtained. Foreground service will be started to capture audio.")

                val audioCaptureIntent = Intent(this, AudioCaptureService::class.java).apply {
                    action = AudioCaptureService.ACTION_START
                    putExtra(AudioCaptureService.EXTRA_RESULT_DATA, data!!)
                }

                viewModel.onStartCapturing()
                startForegroundService(audioCaptureIntent)

            } else {
                binding.root.snackbar("Request to obtain MediaProjection denied.")
            }
        }
    }

    companion object {
        const val LOG_TAG = "MainActivity"
        const val MEDIA_PROJECTION_REQUEST_CODE = 13
    }
}