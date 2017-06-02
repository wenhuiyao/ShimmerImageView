# ShimmerImageView
Add shimmer effect to an image. To use with Views, take a look at [ShimmerView](https://github.com/facebook/shimmer-android)


![Alt text](graphic/shimmer.gif/?raw=true "Optional Title")


## Usage

Java:

```Java

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
```

Kotlin:

```Kotlin

with(shimmerImageView){
    setImageResource(R.drawable.ic_cake_black)
    DrawableCompat.setTint(drawable, Color.parseColor("#CDD1D4"))
    maskSpecs = MaskSpecs(animationDuration = 1200L)
}

startAnimation.setOnClickListener {
    shimmerImageView.startAnimation()
}
```