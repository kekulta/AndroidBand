package com.kekulta.androidband

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.kekulta.androidband.domain.interfacestate.ButtonState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log
import kotlin.math.pow

fun getTimeStamp(): String = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.US).format(Date())


fun ImageButton.bind(
    state: ButtonState,
    @DrawableRes resNormal: Int? = null,
    @DrawableRes resActive: Int? = resNormal
) {
    isEnabled = state.isEnabled
    if (state.isActive != null && resNormal != null && resActive != null) {
        if (state.isActive) {
            setImageResource(resActive)
        } else {
            setImageResource(resNormal)
        }
    }
}

fun View.setMargins(margin: Int) {
    layoutParams = LayoutParams(layoutParams).apply {
        this.setMargins(margin)
    }
}

fun View.getColorStateList(@ColorRes resId: Int): ColorStateList? {
    return ResourcesCompat.getColorStateList(
        resources,
        resId,
        context.theme
    )
}

fun View.getColor(@ColorRes resId: Int): Int {
    return resources.getColor(resId, context.theme)
}

fun View.getThemedColor(@StyleableRes resId: Int): Int {
    var color: Int = Color.BLACK
    onStyleable(R.styleable.ThemeStyle) { color = getColor(resId, Color.BLACK) }
    return color
}

fun View.onStyleable(@StyleableRes resId: IntArray, block: TypedArray.() -> Unit) {
    val ta = context.theme.obtainStyledAttributes(resId)
    ta.block()
    ta.recycle()
}

fun View.getDrawable(@DrawableRes resId: Int): Drawable? {
    return ResourcesCompat.getDrawable(resources, resId, context.theme)
}

fun View.snackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String = "",
    action: ((snackbar: Snackbar) -> Unit)? = null
) {
    Snackbar.make(this, text, duration).apply {
        setAction(actionText) {
            action?.invoke(this)
        }
        show()
    }
}

fun MaterialButtonToggleGroup.disableCheck() {
    addOnButtonCheckedListener { group, _, _ ->
        group.clearChecked()
    }
}

fun <T> List<T>.update(index: Int, producer: (oldItem: T) -> T): List<T> =
    toMutableList().apply { this[index] = producer(this[index]) }

fun <T> List<T>.update(index: Int, el: T): List<T> = toMutableList().apply { this[index] = el }


var MediaPlayer.playbackSpeed: Float
    set(value) {
        playbackParams = playbackParams.setSpeed(value)
    }
    get() = playbackParams.speed

fun MediaPlayer.toggle() {
    if (isPlaying) {
        pause()
    } else {
        start()
    }
}

fun MediaPlayer.setVolumePercent(volume: Float) {
    val log = if (volume == 0f) {
        0f
    } else {
        10.0.pow(-log(100.0 / volume, 2.0)).toFloat()
    }
    setVolume(log, log)
}

fun Slider.disableDrag() {
    setOnTouchListener { _, _ -> true }
}

fun Context.dp(dp: Number): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    )
}

fun View.dp(dp: Number): Float {
    return context.dp(dp)
}

fun Fragment.dp(dp: Number): Float {
    return requireContext().dp(dp)
}

fun View.setHeightDp(dp: Number) {
    layoutParams = layoutParams.apply {
        height = dp(dp).toInt()
    }
}

private class TransformedStateFlow<T>(
    private val getValue: () -> T, private val flow: Flow<T>
) : StateFlow<T> {

    override val replayCache: List<T> get() = listOf(value)
    override val value: T get() = getValue()

    override suspend fun collect(collector: FlowCollector<T>): Nothing =
        coroutineScope { flow.stateIn(this).collect(collector) }
}

/**
 * Returns [StateFlow] from [flow] having initial value from calculation of [getValue]
 */
fun <T> stateFlow(
    getValue: () -> T, flow: Flow<T>
): StateFlow<T> = TransformedStateFlow(getValue, flow)

/**
 * Combines all [stateFlows] and transforms them into another [StateFlow] with [transform]
 */
inline fun <reified T, R> combineStatesInternal(
    vararg stateFlows: StateFlow<T>, crossinline transform: (Array<T>) -> R
): StateFlow<R> = stateFlow(getValue = { transform(stateFlows.map { it.value }.toTypedArray()) },
    flow = combine(*stateFlows) { transform(it) })

/**
 * Variant of [combineStates] for combining 3 state flows
 */
inline fun <reified T1, reified T2, reified T3, R> combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    crossinline transform: (T1, T2, T3) -> R
) = combineStatesInternal(flow1, flow2, flow3) { (t1, t2, t3) ->
    transform(
        t1 as T1, t2 as T2, t3 as T3
    )
}

/**
 * Variant of [combineStates] for combining 4 state flows
 */
inline fun <reified T1, reified T2, reified T3, reified T4, R> combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    crossinline transform: (T1, T2, T3, T4) -> R
) = combineStatesInternal(flow1, flow2, flow3, flow4) { (t1, t2, t3, t4) ->
    transform(
        t1 as T1, t2 as T2, t3 as T3, t4 as T4
    )
}

/**
 * Variant of [combineStates] for combining 5 state flows
 */
inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, R> combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    crossinline transform: (T1, T2, T3, T4, T5) -> R
) = combineStatesInternal(flow1, flow2, flow3, flow4, flow5) { (t1, t2, t3, t4, t5) ->
    transform(
        t1 as T1, t2 as T2, t3 as T3, t4 as T4, t5 as T5
    )
}

/**
 * Does not produce the same value in a raw, so respect "distinct until changed emissions"
 * */
class DerivedStateFlow<T>(
    private val getValue: () -> T, private val flow: Flow<T>
) : StateFlow<T> {

    override val replayCache: List<T>
        get() = listOf(value)

    override val value: T
        get() = getValue()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        coroutineScope { flow.distinctUntilChanged().stateIn(this).collect(collector) }
    }
}

fun <T1, R> StateFlow<T1>.mapState(transform: (a: T1) -> R): StateFlow<R> {
    return DerivedStateFlow(getValue = { transform(this.value) },
        flow = this.map { a -> transform(a) })
}

fun <T1, T2, R> combineStates(
    flow: StateFlow<T1>, flow2: StateFlow<T2>, transform: (a: T1, b: T2) -> R
): StateFlow<R> {
    return DerivedStateFlow(getValue = { transform(flow.value, flow2.value) },
        flow = combine(flow, flow2) { a, b -> transform(a, b) })
}

fun copyInputStreamToFile(inputStream: InputStream, file: File) {
    var out: OutputStream? = null
    try {
        out = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        // Ensure that the InputStreams are closed even if there's an exception.
        try {
            out?.close()

            // If you want to close the "in" InputStream yourself then remove this
            // from here but ensure that you close it yourself eventually.
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}