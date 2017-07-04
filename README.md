# ShimmerImageView
Add shimmer effect to an image. To use with Views, take a look at Facebook's [ShimmerView](https://github.com/facebook/shimmer-android)


![Alt text](graphic/shimmer.gif/?raw=true "Optional Title")


#### Example

Java:

```Java

final ShimmerImageView shimmerView = (ShimmerImageView)findViewById(R.id.shimmerImageView);
shimmerView.setImageResource(R.drawable.ic_cake_black);
DrawableCompat.setTint(shimmerView.getDrawable(), Color.parseColor("#CDD1D4"));

MaskSpecs maskSpecs = new MaskSpecs(); // Can customize the effect with MaskSpecs
maskSpecs.setAnimationDuration(1200L);
shimmerView.setMaskSpecs(maskSpecs);

shimmerView.startAnimation(); // Must call startAnimation()
```

Kotlin:

```Kotlin

with(shimmerImageView) {
    setImageResource(R.drawable.ic_cake_black)
    DrawableCompat.setTint(drawable, Color.parseColor("#CDD1D4"))
    maskSpecs = MaskSpecs(animationDuration = 1200L) // Can customize the effect with MaskSpecs
    startAnimation() // Must call startAnimation()
}
```


#### Usage
gradle:
```Groovy
compile 'com.wenhui:shimmer-imageview:0.3.0'
```