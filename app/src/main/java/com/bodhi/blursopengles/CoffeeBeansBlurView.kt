package com.bodhi.blursopengles

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent


class CoffeeBeansBlurView  : GLSurfaceView {
    private var mActivityContext: Context? = null;
    private lateinit var mCoffeeBeansBlurRenderer: CoffeeBeansBlurRenderer
    constructor(context: Context?) : super(context) {
        init()
        mActivityContext = context
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
        mActivityContext = context
    }

    private fun init() {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        mCoffeeBeansBlurRenderer = CoffeeBeansBlurRenderer(context)
        setRenderer(mCoffeeBeansBlurRenderer)
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                mCoffeeBeansBlurRenderer.mXCoordClicked = event.x
                mCoffeeBeansBlurRenderer.mYCoordClicked = event.y
                requestRender()
            }
        }
        return super.onTouchEvent(event)
    }
}