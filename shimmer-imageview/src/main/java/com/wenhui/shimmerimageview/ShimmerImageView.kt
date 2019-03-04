package com.wenhui.shimmerimageview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import wenhui.com.library.R
import kotlin.properties.Delegates

private const val DEFAULT_MASK_COLOR = 0xFFDDDDDD.toInt()
private const val DEFAULT_INTENSITY = 0f
private const val DEFAULT_DROPOFF = 0.3f
private val DEFAULT_MASK_MODE = PorterDuff.Mode.SRC_IN
private const val DEFAULT_ANIMATION_DURATION = 1200
private const val DEFAULT_START_DELAYED = 0

private fun parseMaskMode(maskModeEnum: Int): PorterDuff.Mode {
    return when (maskModeEnum) {
        3 -> PorterDuff.Mode.SRC_IN
        7 -> PorterDuff.Mode.SRC_ATOP
        9 -> PorterDuff.Mode.MULTIPLY
        15 -> PorterDuff.Mode.SCREEN
        else -> DEFAULT_MASK_MODE
    }
}

/**
 * Specification of the shimmering effect
 */
data class MaskSpecs(val maskColor: Int = DEFAULT_MASK_COLOR,
                     val intensity: Float = DEFAULT_INTENSITY,
                     val dropOff: Float = DEFAULT_DROPOFF,
                     val maskMode: PorterDuff.Mode = DEFAULT_MASK_MODE,
                     val animationDuration: Long = DEFAULT_ANIMATION_DURATION.toLong(),
                     val startDelayed: Long = DEFAULT_START_DELAYED.toLong())

/**
 * Extend ImageView to add shimmer effect, to customize it, use [MaskSpecs].
 *
 * Use [startAnimation] and [stopAnimation] to control the animation
 */
class ShimmerImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    private var maskBitmap: Bitmap? = null
    private var maskRenderCanvas: Canvas? = null
    private var renderMaskBitmap by Delegates.observable<Bitmap?>(null) { _, _, newValue ->
        maskRenderCanvas = newValue?.let { Canvas().apply { setBitmap(newValue) } }
    }

    private var animation: ValueAnimator? = null
    private var maskOffsetX: Float = 0f

    private val widthFloat get() = width.toFloat()
    private val heightFloat get() = height.toFloat()

    private val paint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(maskSpecs.maskMode)
        }
    }

    var maskSpecs: MaskSpecs = MaskSpecs()
        set(value) {
            field = value
            resetAnimation()
        }

    var isAnimationRunning: Boolean = false
        private set

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerImageView)
            val maskColor = a.getColor(R.styleable.ShimmerImageView_maskSpecs_maskColor, DEFAULT_MASK_COLOR)
            val intensity = a.getFloat(R.styleable.ShimmerImageView_maskSpecs_intensity, DEFAULT_INTENSITY)
            val dropOff = a.getFloat(R.styleable.ShimmerImageView_maskSpecs_dropOff, DEFAULT_DROPOFF)
            val maskMode = parseMaskMode(a.getInt(R.styleable.ShimmerImageView_maskSpecs_maskMode, -1))
            val animationDuration = a.getInt(R.styleable.ShimmerImageView_maskSpecs_animationDuration, DEFAULT_ANIMATION_DURATION)
            val startDelayed = a.getInt(R.styleable.ShimmerImageView_maskSpecs_startDelayed, DEFAULT_START_DELAYED)
            val startAnimation = a.getBoolean(R.styleable.ShimmerImageView_startAnimation, false)
            a.recycle()

            maskSpecs = MaskSpecs(
                    maskColor = maskColor,
                    intensity = intensity,
                    dropOff = dropOff,
                    maskMode = maskMode,
                    animationDuration = animationDuration.toLong(),
                    startDelayed = startDelayed.toLong()
            )

            if (startAnimation) {
                startAnimation()
            }
        }
    }

    fun startAnimation() {
        if (isAnimationRunning) return

        if (drawable == null) {
            isAnimationRunning = false
            return
        }

        isAnimationRunning = true
        runAfterLaidOut {
            // If animation is cancelled already, make sure we don't start the animation
            if (!isAnimationRunning) return@runAfterLaidOut

            val startWidth = width + (maskSpecs.startDelayed / maskSpecs.animationDuration.toFloat()).toInt()
            animation = ValueAnimator.ofInt(-(startWidth + width), width).apply {
                duration = maskSpecs.animationDuration + maskSpecs.startDelayed
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    maskOffsetX = (it.animatedValue as Int).toFloat()
                    invalidate()
                }
                start()
            }
        }
    }

    private inline fun runAfterLaidOut(crossinline block: () -> Unit) {
        if (ViewCompat.isLaidOut(this)) {
            block()
            return
        }

        addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                removeOnLayoutChangeListener(this)
                block()
            }
        })
    }

    fun stopAnimation() {
        animation?.cancel()
        animation = null
        isAnimationRunning = false
        maskOffsetX = 0f
        maskBitmap = null
        renderMaskBitmap = null
        invalidate()
    }

    private fun resetAnimation() {
        val animationStarted = isAnimationRunning
        stopAnimation()
        paint.xfermode = PorterDuffXfermode(maskSpecs.maskMode)
        if (animationStarted) {
            startAnimation()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            resetAnimation()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        // Then draw the mask
        drawMaskUsingBitmap(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    private fun drawMaskUsingBitmap(canvas: Canvas?) {
        if (!isAnimationRunning) return

        tryObtainRenderMaskBitmap()?.let {
            drawMaskToBitmap()
            canvas?.drawBitmap(it, 0f, 0f, null)
        }
    }

    private fun tryObtainRenderMaskBitmap() = renderMaskBitmap
            ?: createBitmap(width, height)?.apply { renderMaskBitmap = this }

    private fun drawMaskToBitmap() {
        val bitmap = getMaskBitmap() ?: return
        maskRenderCanvas?.run {
            save()
            clipRect(maskOffsetX, 0f, maskOffsetX + bitmap.width, bitmap.height.toFloat())
            super.draw(this)
            drawBitmap(bitmap, maskOffsetX, 0f, paint)
            restore()
        }
    }

    private fun getMaskBitmap() = maskBitmap ?: createBitmap(width, height)?.apply {
        maskBitmap = this
        val canvas = Canvas(this)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(widthFloat, 0f, 0f, 0f, getGradientColors(), getGradientPositions(), Shader.TileMode.CLAMP)
            colorFilter = PorterDuffColorFilter(maskSpecs.maskColor, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawRect(0f, 0f, widthFloat, heightFloat, paint)
    }

    private fun getGradientColors(): IntArray = intArrayOf(TRANSPARENT, BLACK, BLACK, TRANSPARENT)

    private fun getGradientPositions(): FloatArray {
        return floatArrayOf(Math.max(0f, maskSpecs.dropOff), 0.5f - maskSpecs.intensity / 2f, 0.5f + maskSpecs
                .intensity / 2f, Math.min(1f, 1f - maskSpecs.dropOff))
    }

    private fun createBitmap(width: Int, height: Int): Bitmap? {
        return try {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } catch (ome: OutOfMemoryError) {
            null
        }
    }
}