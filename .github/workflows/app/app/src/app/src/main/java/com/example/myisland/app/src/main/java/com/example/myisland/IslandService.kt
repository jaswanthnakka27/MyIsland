package com.example.myisland

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator

class IslandService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var pill: View
    private lateinit var lp: WindowManager.LayoutParams
    private var open = false

    override fun onBind(i: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val d = resources.displayMetrics.density
        pill = View(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.BLACK); cornerRadius = 100 * d
            }
            setOnClickListener { toggle(d) }
        }
        lp = WindowManager.LayoutParams(
            (120 * d).toInt(), (34 * d).toInt(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = (10 * d).toInt()
        }
        wm.addView(pill, lp)
    }

    private fun toggle(d: Float) {
        val w0 = lp.width; val h0 = lp.height
        val w1 = if (open) (120 * d).toInt() else (340 * d).toInt()
        val h1 = if (open) (34 * d).toInt() else (90 * d).toInt()
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = OvershootInterpolator(0.8f)
            addUpdateListener {
                val t = it.animatedValue as Float
                lp.width = (w0 + (w1 - w0) * t).toInt()
                lp.height = (h0 + (h1 - h0) * t).toInt()
                wm.updateViewLayout(pill, lp)
            }
            start()
        }
        open = !open
    }

    override fun onDestroy() {
        wm.removeView(pill)
        super.onDestroy()
    }
}
