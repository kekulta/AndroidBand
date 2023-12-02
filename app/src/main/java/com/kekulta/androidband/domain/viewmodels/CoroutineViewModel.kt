package com.kekulta.androidband.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class CoroutineViewModel : ViewModel() {
    private val holder = mutableMapOf<Any, Job>()

    fun launchScope(
        scopeId: Any = this,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend CoroutineScope.() -> Unit
    ) {
        holder[scopeId]?.cancel()
        holder[scopeId] = viewModelScope.launch(dispatcher) {
            block()
        }
    }

    fun cancelScope(scopeId: Any): Boolean {
        return if (holder[scopeId] == null) {
            false
        } else {
            holder[scopeId]?.cancel()
            true
        }
    }

    override fun onCleared() {
        super.onCleared()
        holder.forEach { (_, job) -> job.cancel() }
    }
}