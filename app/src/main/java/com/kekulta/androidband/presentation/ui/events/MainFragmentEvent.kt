package com.kekulta.androidband.presentation.ui.events

sealed class MainFragmentEvent {
    data class Input(val inputId: Int, val title: String, val message: String) : MainFragmentEvent()
}