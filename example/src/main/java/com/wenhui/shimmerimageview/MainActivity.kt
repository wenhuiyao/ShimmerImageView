package com.wenhui.shimmerimageview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shimmerImageView.maskSpecs = MaskSpecs(animationDuration = 1000L)

        startAnimationButton.setOnClickListener {
            if (shimmerImageView.isAnimationRunning()) {
                startAnimationButton.text = "Start Animation"
                shimmerImageView.stopAnimation()
            } else {
                startAnimationButton.text = "Stop Animation"
                shimmerImageView.startAnimation()
            }
        }
    }
}
