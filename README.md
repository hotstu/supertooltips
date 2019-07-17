[![author](https://img.shields.io/badge/author-hglf-blue.svg)](https://github.com/hotstu) [ ![Download](https://api.bintray.com/packages/hglf/maven/supertooltips/images/download.svg) ](https://bintray.com/hglf/maven/supertooltips/_latestVersion) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

SuperToolTips (reborned)
===========
### why this fork(hotstu/supertooltips)?
the origin one is five years old and discontinued with bug left & old school dependencies 
### what's new in this fork(hotstu/supertooltips)↓↓↓
* Rewrite in kotlin
* bug fix
* better performance (by reducing depth of view tree )
* target api 28
* no need  nineoldandroid library
* kotlin friendly api

=======================


SuperToolTips is an Open Source Android library that allows developers to easily create Tool Tips for views.
Feel free to use it all you want in your Android apps provided that you cite this project and include the license in your app.

Setup
-----
*Note: SuperToolTips now uses the gradle build structure. If you want to use this project in Eclipse, you should make the necessary changes.*

Add the following to your `build.gradle`:


```groovy
dependencies {
    implemention 'github.hotstu.supertooltips:lib:1.0.0'
}

```
Usage
-----

* (optional) In your layout xml file, add the `ToolTipRelativeLayout` (`com.nhaarman.supertooltips.ToolTipRelativeLayout`) with height and width of `match_parent`. Make sure this view is on top!
* Find the `ToolTipRelativeLayout` in your code, and start adding `ToolTips`!

Example:
-----

```xml
<RelativeLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<TextView
	    android:id="@+id/activity_main_redtv"
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content"
	    android:layout_centerInParent="true" />

	<github.hotstu.supertooltips.ToolTipContainer
		android:id="@+id/activity_main_tooltipRelativeLayout"
		android:layout_width="match_parent"
		android:layout_height="match_parent" />
</RelativeLayout>
```

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
	mToolTipFrameLayout = findViewById<View>(R.id.activity_main_tooltipframelayout) as ToolTipContainer
    val toolTip = ToolTip().apply {
        text = ("Moarrrr buttons!")
        color = (resources.getColor(R.color.holo_blue))
        animationType = (AnimationType.FROM_TOP)
    }
    mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_bluetv))		
}
```
	

ToolTip customization
-----
You can customize the `ToolTip` in several ways:

* Specify a content text using `ToolTip.setText()`.
* Set a color using `ToolTip.setColor()`.
* Specify whether to show a shadow or not with `ToolTip.setShadow()`.
* Specify how to animate the ToolTip: from the view itself or from the top, using `ToolTip.setAnimationType()`.
* Set your own custom content View using `ToolTip.setContentView()`.

See the examples.




