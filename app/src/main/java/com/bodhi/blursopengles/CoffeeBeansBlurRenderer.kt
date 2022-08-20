package com.bodhi.blursopengles

import android.content.Context
import android.content.res.Configuration
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CoffeeBeansBlurRenderer (private val mActivityContext: Context)  : GLSurfaceView.Renderer {
    private lateinit var mTwodScreen: TwoDimensionalScreen
    private var mWidth = 0
    private var mHeight = 0

    var mXCoordClicked = 0.0f
    var mYCoordClicked = 0.0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES31.glClearColor(0f, 0f, 0f, 1f)
        mTwodScreen = TwoDimensionalScreen(mActivityContext)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }
    override fun onDrawFrame(gl: GL10) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)
        val orientation: Int = mActivityContext.resources.configuration.orientation
        var isPortrait = true
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false
        }
        mTwodScreen.draw(gl, mWidth, mHeight, isPortrait, mXCoordClicked,
            mYCoordClicked)
    }
}