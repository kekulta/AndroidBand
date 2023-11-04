package com.kekulta.androidband.domain.audio.visualizer

interface AudioVisualizer {
    fun init()
    fun getWaveForm(): Int
}