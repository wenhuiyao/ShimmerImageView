# ShimmerImageView
Add shimmer effect to an image. To use with Views, take a look at Facebook's [ShimmerView](https://github.com/facebook/shimmer-android)


![Alt text](graphic/shimmer.gif/?raw=true "Optional Title")


#### Example

Usage

```XML
<com.wenhui.shimmerimageview.ShimmerImageView
    ......
    app:startAnimation="true"
    app:maskSpecs_maskMode="src_in"
    app:maskSpecs_dropOff="0.3"
    app:maskSpecs_maskColor="#DDDDDD"
    app:maskSpecs_animationDuration="2000"
    app:maskSpecs_intensity="0.1"
    app:maskSpecs_startDelayed="0"
    />
```

#### Usage
gradle:
```Groovy
compile 'com.wenhui:shimmer-imageview:0.4.0'
```