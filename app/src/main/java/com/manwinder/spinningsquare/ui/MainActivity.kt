package com.manwinder.spinningsquare.ui

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.manwinder.spinningsquare.R
import com.manwinder.spinningsquare.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val dateFormatToShow = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupTimeTextViewOnTouchListener()

        val retrieveTime = Runnable { getTime() }

        val executor = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(retrieveTime , 0, 1, TimeUnit.MILLISECONDS)
    }

    private fun getTime() {
        try {
            val jsonObject = NetworkUtils.getJSONObjectFromURL(getString(R.string.date_time_url))
            val date = dateFormatter.parse(jsonObject.get("datetime").toString())

            time_tv.text = dateFormatToShow.format(date).toString()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setupTimeTextViewOnTouchListener() {
        time_tv.onTouch { event, keepDrawerOpen ->
            setDrawerHeightForTimeTextView()

            if (event?.action == MotionEvent.ACTION_DOWN) {
                expandOrShrinkView(drawer_v, 250f)
            } else if (event?.action == MotionEvent.ACTION_UP && !keepDrawerOpen) {
                expandOrShrinkView(drawer_v, 50f)
            }
        }
    }

    private fun expandOrShrinkView(view: View, drawerDpHeight: Float) {
        val anim = ValueAnimator.ofInt(view.measuredHeight, getDrawerHeight(drawerDpHeight).toInt())

        anim.addUpdateListener { valueAnimator ->
            val height = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = height
            view.layoutParams = layoutParams
        }
        anim.duration = 500
        anim.start()
    }

    private fun setDrawerHeightForTimeTextView() {
        time_tv.deviceHeight = getDeviceHeight().toFloat() - getStatusBarHeight(this)
        time_tv.deviceWidth = getDeviceWidth().toFloat()
        time_tv.drawerFullHeight = getDrawerHeight(250f)
        time_tv.drawerHeight = drawer_v.measuredHeight.toFloat()
    }

    private fun getDrawerHeight(dpHeight: Float): Float {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, displayMetrics)
    }

    private fun getDeviceHeight(): Int {
        return getDeviceDimensions().heightPixels
    }

    private fun getDeviceWidth(): Int {
        return getDeviceDimensions().widthPixels
    }

    private fun getDeviceDimensions(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0  && Build.VERSION.SDK_INT != Build.VERSION_CODES.P)
            resources.getDimensionPixelSize(resourceId)
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Math.ceil((25 * resources.displayMetrics.density).toDouble()).toInt()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Math.ceil((24 * resources.displayMetrics.density).toDouble()).toInt()
        } else 0
    }
}
