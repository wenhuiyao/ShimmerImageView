package com.wenhui.shimmerimageview;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.View;

public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ShimmerImageView shimmerView = findViewById(R.id.shimmerImageView);
        shimmerView.setImageResource(R.drawable.ic_cake_black);
        DrawableCompat.setTint(shimmerView.getDrawable(), Color.parseColor("#CDD1D4"));

        MaskSpecs maskSpecs = new MaskSpecs();
        maskSpecs.setAnimationDuration(1200L);
        shimmerView.setMaskSpecs(maskSpecs);

        findViewById(R.id.startAnimation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shimmerView.startAnimation();
            }
        });
    }
}
