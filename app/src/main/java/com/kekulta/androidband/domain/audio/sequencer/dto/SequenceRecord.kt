package com.kekulta.androidband.domain.audio.sequencer.dto

import com.kekulta.androidband.presentation.ui.vo.SampleVo

data class SequenceRecord(
    val initialState: List<SampleVo>,
    val events: List<SequenceEvent>,
)