package com.kekulta.androidband.presentation.ui.events

import java.io.File

sealed class LibraryFragmentEvent {
    data class Share(val file: File) : LibraryFragmentEvent()
    data class Input(val inputId: Int, val title: String, val message: String) :
        LibraryFragmentEvent()
}