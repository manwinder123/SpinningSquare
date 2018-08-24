package com.manwinder.spinningsquare.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.manwinder.spinningsquare.R


class TimeTextView: TextView {

    private var dX = 0f
    private var dY = 0f

    private var originalXPos = 0f
    private var originalYPos = 0f

    private var rotateAngle = 0f
    private val rotateXPivot by lazy { 0.5f * width }
    private val rotateYPivot by lazy { 0.5f * height }

    private val paint = Paint().apply { this.color = Color.parseColor(context.getString(R.string.baby_blue_color)) }
    private val borderPaint = Paint().apply {
        this.color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }
    private val rectF by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }

    var drawerFullHeight: Float = 0f
    var drawerHeight: Float = 0f

    var deviceHeight: Float = 0f
    var deviceWidth: Float = 0f

    lateinit var onTouch:(event: MotionEvent?, keepDrawerOpen: Boolean) -> Unit

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setupRotationAnimation()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupRotationAnimation()
    }

    constructor(context: Context) : super(context) {
        setupRotationAnimation()
    }

    private fun setupRotationAnimation() {
        val rotateAnimator= ValueAnimator.ofFloat(0f, 360f)

        rotateAnimator.addUpdateListener { animation ->
            val angle : Float = animation.animatedValue as Float
            rotateAngle = angle
            invalidate()
        }
        rotateAnimator.duration = 1000
        rotateAnimator.repeatCount = ValueAnimator.INFINITE
        rotateAnimator.interpolator = LinearInterpolator()
        rotateAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != 0 && h != 0) {
            originalXPos = x
            originalYPos = y
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = x - event.rawX
                dY = y - event.rawY
                bringToFront()
                onTouch(event, false)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                x = event.rawX + dX
                y = event.rawY + dY
                invalidate()
                onTouch(event, false)
                true
            }
            MotionEvent.ACTION_UP -> {
                if ((y + height*0.75f) > (deviceHeight - drawerHeight) && drawerHeight > height) {
                    animate().x(deviceWidth/2 - width/2)
                            .y(deviceHeight - drawerFullHeight/2 - height/2)
                            .setDuration(500)
                            .start()
                    onTouch(event, true)
                } else {
                    animate().x(originalXPos)
                            .y(originalYPos)
                            .setDuration(500)
                            .start()
                    onTouch(event, false)
                }
                true
            }
            else -> false
        }
    }

    fun onTouch(onTouch: (event: MotionEvent?, keepDrawerOpen: Boolean) -> Unit) {
        this.onTouch = onTouch
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()
        canvas?.rotate(rotateAngle, rotateXPivot, rotateYPivot)
        canvas?.drawRect(rectF, borderPaint)
        canvas?.drawRect(rectF, paint)
        super.onDraw(canvas)
        canvas?.restore()
    }
}