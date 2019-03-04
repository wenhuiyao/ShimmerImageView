package com.wenhui.shimmerimageview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import kotlin.properties.Delegates

/**
 * Specification of the shimmering effect
 */
data class MaskSpecs(var maskColor: Int = 0xFFDDDDDD.toInt(),
                     var intensity: Float = 0f,
                     var dropOff: Float = 0.3f,
                     var maskMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN,
                     var animationDuration: Long = 1200L,
                     var startDelayed: Long = 0L)

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


    fun startAnimation() {
        if (drawable == null || isAnimationRunning()) return

        runAfterLaidOut {
            val startWidth = width + (maskSpecs.startDelayed / maskSpecs.animationDuration.toFloat()).toInt()
            ValueAnimator.ofInt(-(startWidth + width), width).run {
                animation = this
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
        } else {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    block()
                    return false
                }
            })
        }
    }

    fun stopAnimation() {
        animation?.cancel()
        animation = null
        invalidate()
    }

    fun isAnimationRunning() = animation != null

    private fun resetAnimation() {
        val animationStarted = isAnimationRunning()
        stopAnimation()
        paint.xfermode = PorterDuffXfermode(maskSpecs.maskMode)
        maskBitmap = null
        renderMaskBitmap = null
        if (animationStarted) {
            startAnimation()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        resetAnimation()
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
        if (!isAnimationRunning()) return

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
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } catch (ome: OutOfMemoryError) {
            return null
        }
    }
}