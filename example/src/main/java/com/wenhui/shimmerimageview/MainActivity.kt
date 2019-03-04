package com.wenhui.shimmerimageview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateStartAnimationButton()
        startAnimationButton.setOnClickListener {
            if (shimmerImageView.isAnimationRunning) {
                shimmerImageView.stopAnimation()
            } else {
                shimmerImageView.startAnimation()
            }
            updateStartAnimationButton()
        }
    }

    private fun updateStartAnimationButton() {
        if (shimmerImageView.isAnimationRunning) {
            startAnimationButton.text = "Stop Animation"
        } else {
            startAnimationButton.text = "Start Animation"
        }
    }

}
