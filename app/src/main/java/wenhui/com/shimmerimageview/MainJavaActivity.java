package wenhui.com.shimmerimageview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.android.wenhui.MaskSpecs;
import com.android.wenhui.ShimmerImageView;

/**
 * Created by wyao on 6/1/17.
 */

public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ShimmerImageView shimmerView = (ShimmerImageView)findViewById(R.id.shimmerImageView);
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
