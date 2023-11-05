package com.kekulta.androidband.presentation.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import androidx.annotation.LayoutRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kekulta.androidband.R
import com.kekulta.androidband.presentation.framework.RationaleCallback

fun Context.showProgress(title: String, block: () -> Unit) {
    val progressView = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null)
    val dialog = MaterialAlertDialogBuilder(this).apply {
        setCancelable(false)
        setTitle(title)
        setView(progressView)
    }.show()

    block()
    dialog.dismiss()

    return
}

fun Context.showHelpPage(title: String, @LayoutRes resId: Int) {
    val helpPage = LayoutInflater.from(this).inflate(resId, null)
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setView(helpPage)
        setPositiveButton(getString(R.string.default_help_page_accept)) { dialog, _ ->
            dialog.dismiss()
        }
        show()
    }
}

fun Context.showInput(
    title: String,
    message: String,
    acceptText: String = getString(R.string.default_input_accept),
    closeText: String? = getString(R.string.default_input_close),
    inputCallback: (String?) -> Unit
) {
    val editText = EditText(this)
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setView(editText)
        setMessage(message)
        if (closeText != null) {
            setNeutralButton(closeText) { dialog, _ ->
                inputCallback(null)
                dialog.dismiss()
            }
        }
        setPositiveButton(acceptText) { dialog, _ ->
            inputCallback(editText.text?.toString())
            dialog.dismiss()
        }
        show()
    }
}

fun Context.formRationale(
    title: String,
    message: String,
    acceptText: String = getString(R.string.default_rationale_accept),
    rejectText: String = getString(R.string.default_rationale_reject),
    closeText: String? = getString(R.string.default_rationale_close),
): RationaleCallback {
    return { permissionRequest ->
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            if (closeText != null) {
                setNeutralButton(closeText) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            setNegativeButton(rejectText) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(acceptText) { dialog, _ ->
                permissionRequest()
                dialog.dismiss()
            }
            show()
        }
    }
}