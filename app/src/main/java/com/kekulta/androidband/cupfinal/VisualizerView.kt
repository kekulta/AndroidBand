package com.kekulta.androidband.cupfinal

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.kekulta.androidband.dp

class VisualizerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val colorEvaluator = ArgbEvaluator()
    private var currentColor:Int = Color.parseColor("#5A50E2")

    fun visualize(props: AnimationProperties) {
        ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat("translationX", dp(props.translationX)),
            PropertyValuesHolder.ofFloat("translationY", dp(props.translationY)),
            PropertyValuesHolder.ofFloat("scaleX", props.scaleX),
            PropertyValuesHolder.ofFloat("scaleY", props.scaleY),
            PropertyValuesHolder.ofFloat("rotation", props.rotation)
        ).run()

        props.color?.let { color ->
            ObjectAnimator.ofObject(
                this, "colorFilter", colorEvaluator, currentColor, color
            ).run()
            currentColor = color
        }
    }

    private fun ObjectAnimator.run() {
        duration = VIS_PERIOD_MILLIS * SKIP_FRAMES
        start()
    }

    companion object {
        const val LOG_TAG = "VisualizerView"
    }
}

data class AnimationProperties(
    val translationX: Float = 0f,
    val translationY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val color: Int? = null
)