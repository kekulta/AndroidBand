package com.kekulta.androidband.presentation.framework

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.SEEK_CLOSEST_SYNC
import android.net.Uri
import android.util.Log
import com.kekulta.androidband.domain.audio.samples.SamplePlayer
import com.kekulta.androidband.playbackSpeed
import com.kekulta.androidband.setVolumePercent


class LoopSamplePlayer private constructor(
    context: Context,
    audioSessionId: AudioSessionId,
    // At least resId or Uri must be provided!
    resId: Int? = null,
    uri: Uri? = null,
) : SamplePlayer {

    override var duration = -1
        private set
    override var currentPosition = 0
        private set
        get() {
            actualPlayer { field = currentPosition }
            return field
        }
    override var speed: Float = 1f
        private set
    override var volume = 100f
        private set
    override var isPlaying: Boolean = false
        private set
    override var isSoundOn: Boolean = true
        private set

    private var mCounter = 1
    private var mCurrentPlayer: MediaPlayer? = null
    private var mNextPlayer: MediaPlayer? = null
    private val onCompletionListener =
        OnCompletionListener { mediaPlayer ->
            mediaPlayer.release()
            mCurrentPlayer = mNextPlayer
            mCurrentPlayer!!.playbackSpeed = speed
            if (isSoundOn) {
                mCurrentPlayer!!.setVolumePercent(volume)
            } else {
                mCurrentPlayer!!.setVolumePercent(0f)
            }
            createNextMediaPlayer()
            Log.d(LOG_TAG, String.format("Loop #%d", ++mCounter))
        }

    private val mContext: Context
    private val mResId: Int?
    private val mUri: Uri?
    private val mAudioSessionId: AudioSessionId

    init {
        mContext = context
        mAudioSessionId = audioSessionId
        mResId = resId
        mUri = uri


        mCurrentPlayer = createPlayer()
        mCurrentPlayer!!.playbackSpeed = speed
        pause()
        mCurrentPlayer!!.setVolumePercent(volume)
        duration = mCurrentPlayer!!.duration
        createNextMediaPlayer()
    }


    override fun start() {
        isPlaying = true
        actualPlayer {
            start()
            setSpeed(speed)
        }
    }

    override fun pause() {
        isPlaying = false
        actualPlayer { pause() }
    }

    override fun toggle() {
        if (isPlaying) {
            pause()
        } else {
            start()
        }
    }

    override fun seekTo(millis: Long) {
        actualPlayer { seekTo(millis, SEEK_CLOSEST_SYNC) }
    }

    override fun setSpeed(speed: Float) {
        this.speed = speed
        actualPlayer {
            if (isPlaying) this.playbackSpeed = speed
        }
    }

    override fun setVolume(volume: Float) {
        this.volume = volume
        if (isSoundOn) {
            actualPlayer { setVolumePercent(volume) }
        }
    }

    override fun toggleSound(isSoundOn: Boolean) {
        if (isSoundOn) {
            setSoundOn()
        } else {
            setSoundOff()
        }
    }

    override fun release() {
        isPlaying = false
        mCurrentPlayer?.release()
        mNextPlayer?.release()
    }

    private fun createPlayer(): MediaPlayer {
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build()
        return when {
            mResId != null -> MediaPlayer.create(
                mContext,
                mResId,
                audioAttributes,
                mAudioSessionId.sessionId
            )

            mUri != null -> MediaPlayer.create(
                mContext,
                mUri,
                null,
                audioAttributes,
                mAudioSessionId.sessionId
            )

            else -> throw IllegalArgumentException("Can't create MediaPlayer, uri or resId must be provided!")
        }
    }

    private fun setSoundOn() {
        isSoundOn = true
        actualPlayer { setVolumePercent(volume) }
    }

    private fun setSoundOff() {
        isSoundOn = false
        actualPlayer { setVolumePercent(0f) }
    }

    private fun createNextMediaPlayer() {
        try {
            mNextPlayer = createPlayer()
            mCurrentPlayer!!.setNextMediaPlayer(mNextPlayer)
            mCurrentPlayer!!.setOnCompletionListener(onCompletionListener)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Looks like sound have been deleted!")
            e.printStackTrace()
        }
    }

    private fun actualPlayer(block: MediaPlayer.() -> Unit) {
        return try {
            mCurrentPlayer!!.block()
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                mNextPlayer!!.block()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val LOG_TAG = "LoopMediaPlayer"
        fun create(context: Context, resId: Int, audioSessionId: AudioSessionId): SamplePlayer {
            return LoopSamplePlayer(
                context = context,
                resId = resId,
                uri = null,
                audioSessionId = audioSessionId
            )
        }

        fun create(context: Context, uri: Uri, audioSessionId: AudioSessionId): SamplePlayer {
            return LoopSamplePlayer(
                context = context,
                resId = null,
                uri = uri,
                audioSessionId = audioSessionId
            )
        }
    }
}