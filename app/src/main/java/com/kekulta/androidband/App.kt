package com.kekulta.androidband

import android.app.Application
import android.media.MediaPlayer
import com.kekulta.androidband.di.koinModule
import com.kekulta.androidband.di.viewModelModule
import com.kekulta.androidband.presentation.framework.AudioSessionId
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(instance)
            modules(
                getSessionIdModule(),
                viewModelModule,
                koinModule,
            )
        }
    }

    private fun getSessionIdModule(): Module {
        val sessionId: Int
        // Create correct sessionId for MediaPlayers, it could break Visualizer otherwise
        MediaPlayer.create(this, R.raw.sample_melody_one).apply {
            sessionId = audioSessionId
            release()
        }

        return module {
            single { AudioSessionId(sessionId) }
        }
    }

    companion object {
        lateinit var instance: App
            private set
        const val AUDIO_PROVIDER_AUTHORITY = "com.kekulta.audioprovider"
    }
}