package com.kekulta.androidband.presentation.framework

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(
    private val context: Context,
) {
    fun getString(@StringRes resId: Int): String {
        return context.resources.getString(resId)
    }
}