package com.kekulta.androidband.di

import com.kekulta.androidband.data.PersistenceManager
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.data.db.AppDatabase
import com.kekulta.androidband.domain.audio.capture.CaptureRepository
import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.audio.samples.SampleVoFormatter
import com.kekulta.androidband.domain.audio.samples.SamplesFactory
import com.kekulta.androidband.domain.audio.samples.SamplesRepository
import com.kekulta.androidband.domain.audio.sequencer.SampleFrameMapper
import com.kekulta.androidband.domain.audio.sequencer.SequencePlayer
import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.domain.audio.sequencer.SequenceRenderer
import com.kekulta.androidband.domain.audio.sounds.GetSoundsListUseCase
import com.kekulta.androidband.domain.audio.sounds.QuickSoundsManager
import com.kekulta.androidband.domain.audio.visualizer.AudioVisualizer
import com.kekulta.androidband.domain.audio.visualizer.VisualizerRepository
import com.kekulta.androidband.domain.interfacestate.ButtonsStateUseCase
import com.kekulta.androidband.domain.viewmodels.LibraryFragmentViewModel
import com.kekulta.androidband.domain.viewmodels.MainFragmentViewModel
import com.kekulta.androidband.domain.viewmodels.MainViewModel
import com.kekulta.androidband.presentation.framework.AndroidVisualizer
import com.kekulta.androidband.presentation.framework.AssetManager
import com.kekulta.androidband.presentation.framework.FilesManager
import com.kekulta.androidband.presentation.framework.MicRecordingRepository
import com.kekulta.androidband.presentation.framework.PermissionManager
import com.kekulta.androidband.presentation.framework.ResourceManager
import com.kekulta.androidband.presentation.framework.SamplesFactoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {
    single<AudioVisualizer> { AndroidVisualizer(get()) }
    single { PersistenceManager(get(), get(), get()) }
    single { AppDatabase.createDatabase(get()) }
    single { SamplesRepository(get(), get()) }
    single { QuickSoundsManager(get()) }
    single { SoundsDataStore(get(), get()) }
    single { get<AppDatabase>().getSoundDao() }
    single { get<AppDatabase>().getSamplesDao() }
    single { VisualizerRepository(get()) }
    single { SequenceRecorder() }
    single { SequencePlayer(get(), get()) }
    single { SampleManager(get(), get(), get(), get(), get()) }
    single { SequenceRenderer(get(), get()) }
    single { CaptureRepository() }
    single { PermissionManager(get()) }
    single { MicRecordingRepository(get(), get()) }

    factory<SamplesFactory> { SamplesFactoryImpl(get(), get(), get()) }
    factory { ResourceManager(get()) }
    factory { AssetManager(get(), get(), get()) }
    factory { SampleVoFormatter(get()) }
    factory { SampleFrameMapper() }
    factory { FilesManager(get()) }
    factory { ButtonsStateUseCase(get(), get(), get(), get(), get()) }
    factory { GetSoundsListUseCase(get(), get()) }
}

val viewModelModule = module {
    viewModel {
        MainFragmentViewModel(
            sampleManager = get(),
            quickSoundsManager = get(),
            visualizerRepository = get(),
            recorder = get(),
            player = get(),
            buttonsStateUseCase = get(),
            sequenceRenderer = get(),
            sampleFrameMapper = get(),
            soundsDataStore = get(),
            captureRepository = get(),
            permissionManager = get(),
            micRecordingRepository = get(),
            resourceManager = get(),
            persistenceManager = get(),
        )
    }

    viewModel {
        MainViewModel(
            captureRepository = get(), permissionManager = get()
        )
    }

    viewModel {
        LibraryFragmentViewModel(
            sampleManager = get(),
            getSoundsListUseCase = get(),
            quickSoundsManager = get(),
            soundsDataStore = get(),
            resourceManager = get(),
        )
    }
}