package com.wenhui.shimmerimageview

import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(shimmerImageView){
            setImageResource(R.drawable.ic_cake_black)
            DrawableCompat.setTint(drawable, Color.parseColor("#CDD1D4"))
            maskSpecs = MaskSpecs(animationDuration = 1200L)
        }

        startAnimation.setOnClickListener {
            shimmerImageView.startAnimation()
        }
    }
}
