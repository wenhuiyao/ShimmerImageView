package wenhui.com.shimmerimageview

import android.graphics.Color
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import com.android.wenhui.MaskSpecs
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
