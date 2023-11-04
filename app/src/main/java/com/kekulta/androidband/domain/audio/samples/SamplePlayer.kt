package com.kekulta.androidband.domain.audio.samples

interface SamplePlayer {
    val duration: Int
    val currentPosition: Int
    val speed: Float
    val volume: Float
    val isPlaying: Boolean
    val isSoundOn: Boolean

    fun start()
    fun pause()
    fun toggle()
    fun seekTo(millis: Long)
    fun setSpeed(speed: Float)
    fun setVolume(speed: Float)
    fun toggleSound(isSoundOn: Boolean)
    fun release()

}