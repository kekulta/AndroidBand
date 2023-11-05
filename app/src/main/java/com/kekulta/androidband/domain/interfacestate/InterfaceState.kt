package com.kekulta.androidband.domain.interfacestate

data class InterfaceState(
    val sampleOneButtonState: ButtonState,
    val sampleTwoButtonState: ButtonState,
    val sampleThreeButtonState: ButtonState,
    val sampleFourButtonState: ButtonState,
    val libButtonState: ButtonState,
    val micRecordingButtonState: ButtonState,
    val captureButtonState: ButtonState,
    val recordButtonState: ButtonState,
    val recordPlayButtonState: ButtonState,
    val renderButtonState: ButtonState,
)