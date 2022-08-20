package com.bodhi.blursopengles

import android.content.Context
import android.opengl.GLES31
import com.bodhi.blursopengles.RawResourceReader.readTextFileFromRawResource
import com.bodhi.blursopengles.TextureHelper.loadTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10


class TwoDimensionalScreen(private val mActivityContext: Context)  {


    private val mVertexBufferFirstPass: FloatBuffer
    private val mTextureBufferFirstPass: FloatBuffer

    private val mVertexBufferSecondPass: FloatBuffer
    private val mTextureBufferSecondPass: FloatBuffer

    private val mProgramFirstPass: Int
    private val mProgramSecondPass:Int
    private var mPositionHandle = 0
    private var mTextureCoordinateHandle = 0
    private var mTextureDataHandle = 0
    private var mTextureUniformHandle = 0
    private var mIsPortraitShaderHandle = 0
    private var mIsPortraitShader = 0

    private var mViewWidthShader = 0.0f
    private var mViewWidthShaderHandle = 0

    private var mViewHeightShader = 0.0f
    private var mViewHeightShaderHandle = 0

    private var mAspectRatioShader = 3000.0f/2000.0f
    private var mAspectRatioShaderHandle = 0

    private var mClickXCoordShader = 0.0f
    private var mClickXCoordShaderHandle = 0

    private var mKernelHalfLengthShader = 0.0f
    private var mKernelHalfLengthShaderHandle = 0

    private var mBlurRadiusShader = 0.0f
    private var mBlurRadiusShaderHandle = 0

    private var mProcessedTextureHandle = 0

    private var mWidth = 0
    private var mHeight = 0



    private var mClickYCoordShader = 0.0f
    private var mClickYCoordShaderHandle = 0

    private val mVertexCount = sTriangleCoords.size / COORDS_PER_VERTEX
    private val mVertexStride = COORDS_PER_VERTEX * 4
    private var mTextureOutputHandle = 0
    private val mFrameBuffer: IntBuffer

    private val mVertexShaderCodeFirstPass: String? = readTextFileFromRawResource(
            mActivityContext,
            R.raw.first_pass_vertex_shader
        )
    private val mFragmentShaderCodeFirstPass: String? = readTextFileFromRawResource(
            mActivityContext,
            R.raw.first_pass_fragment_shader
        )

    private val mVertexShaderCodeSecondPass: String? = readTextFileFromRawResource(
        mActivityContext,
        R.raw.second_pass_vertex_shader
    )
    private val mFragmentShaderCodeSecondPass: String? = readTextFileFromRawResource(
        mActivityContext,
        R.raw.second_pass_fragment_shader
    )

    fun doFirstPass(width: Int, height: Int, isPortrait: Boolean){

        GLES31.glUseProgram(mProgramFirstPass)
        GLES31.glViewport(0, 0, width, height)

        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, mFrameBuffer.get(0))
        GLES31.glFramebufferTexture2D(GLES31.GL_FRAMEBUFFER, GLES31.GL_COLOR_ATTACHMENT0, GLES31.GL_TEXTURE_2D, mTextureOutputHandle, 0)

        mPositionHandle = GLES31.glGetAttribLocation(mProgramFirstPass, "a_Position")
        mTextureCoordinateHandle = GLES31.glGetAttribLocation(mProgramFirstPass, "a_TexCoordinate")
        mTextureUniformHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_Texture")
        mIsPortraitShaderHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_IsPortrait")
        mViewHeightShaderHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_ViewHeight")
        mViewWidthShaderHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_ViewWidth")
        mAspectRatioShaderHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_AspectRatio")
        mKernelHalfLengthShaderHandle = GLES31.glGetUniformLocation(mProgramFirstPass, "u_KernelHalfLength")

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, mTextureDataHandle)
        GLES31.glUniform1i(mTextureUniformHandle, 0)

        if(isPortrait){
            mIsPortraitShader = 1
        } else {
            mIsPortraitShader = 0
        }

        GLES31.glUniform1i(mIsPortraitShaderHandle, mIsPortraitShader)

        GLES31.glUniform1f(mViewWidthShaderHandle, width.toFloat())

        GLES31.glUniform1f(mViewHeightShaderHandle, height.toFloat())

        GLES31.glUniform1f(mAspectRatioShaderHandle, mAspectRatioShader)

        GLES31.glUniform1f(mKernelHalfLengthShaderHandle, Config.halfKernelLength)

        GLES31.glEnableVertexAttribArray(mTextureCoordinateHandle)

        GLES31.glVertexAttribPointer(
            mTextureCoordinateHandle, 2, GLES31.GL_FLOAT, false,
            0, mTextureBufferFirstPass
        )

        GLES31.glEnableVertexAttribArray(mPositionHandle)

        GLES31.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES31.GL_FLOAT, false,
            mVertexStride, mVertexBufferFirstPass
        )

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, mVertexCount)

        GLES31.glDisableVertexAttribArray(mPositionHandle)
        GLES31.glDisableVertexAttribArray(mTextureCoordinateHandle)

    }

    fun doSecondPass(width: Int, height: Int, isPortrait: Boolean,
        xCoordClicked: Float, yCoordClicked: Float){

        GLES31.glUseProgram(mProgramSecondPass)
        GLES31.glViewport(0, 0, width, height)
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0)

        mPositionHandle = GLES31.glGetAttribLocation(mProgramSecondPass, "a_Position")
        mTextureCoordinateHandle = GLES31.glGetAttribLocation(mProgramSecondPass, "a_TexCoordinate")
        mTextureUniformHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_Texture")
        mProcessedTextureHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_ProcessedTexture")
        mIsPortraitShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_IsPortrait")
        mViewHeightShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_ViewHeight")
        mViewWidthShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_ViewWidth")
        mAspectRatioShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_AspectRatio")
        mKernelHalfLengthShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_KernelHalfLength")

        mClickXCoordShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_ClickXCoord")
        mClickYCoordShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_ClickYCoord")

        mBlurRadiusShaderHandle = GLES31.glGetUniformLocation(mProgramSecondPass, "u_BlurRadius")

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, mTextureDataHandle)
        GLES31.glUniform1i(mTextureUniformHandle, 0)

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0+1)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, mTextureOutputHandle)
        GLES31.glUniform1i(mProcessedTextureHandle, 1)

        if(isPortrait){
            mIsPortraitShader = 1
        } else {
            mIsPortraitShader = 0
        }

        GLES31.glUniform1i(mIsPortraitShaderHandle, mIsPortraitShader)

        GLES31.glUniform1f(mViewWidthShaderHandle, width.toFloat())

        GLES31.glUniform1f(mViewHeightShaderHandle, height.toFloat())

        GLES31.glUniform1f(mAspectRatioShaderHandle, mAspectRatioShader)

        GLES31.glUniform1f(mClickXCoordShaderHandle, xCoordClicked)

        GLES31.glUniform1f(mClickYCoordShaderHandle, yCoordClicked)

        GLES31.glUniform1f(mKernelHalfLengthShaderHandle, Config.halfKernelLength)

        GLES31.glUniform1f(mBlurRadiusShaderHandle, Config.radius)

        GLES31.glEnableVertexAttribArray(mTextureCoordinateHandle)

        GLES31.glVertexAttribPointer(
            mTextureCoordinateHandle, 2, GLES31.GL_FLOAT, false,
            0, mTextureBufferSecondPass
        )

        GLES31.glEnableVertexAttribArray(mPositionHandle)

        GLES31.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES31.GL_FLOAT, false,
            mVertexStride, mVertexBufferSecondPass
        )

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, mVertexCount)

        GLES31.glDisableVertexAttribArray(mPositionHandle)
        GLES31.glDisableVertexAttribArray(mTextureCoordinateHandle)

    }

    fun draw(gl: GL10, width: Int, height: Int, isPortrait: Boolean,
             xCoordClicked: Float, yCoordClicked: Float){
        if(mWidth != width || mHeight != height){
            mTextureOutputHandle = TextureHelper.configureTextureOutput(width, height)
            doFirstPass(width, height, isPortrait)
            mWidth = width
            mHeight = height
        }

        doSecondPass(width, height, isPortrait, xCoordClicked, yCoordClicked)
    }

    companion object {
        const val COORDS_PER_VERTEX = 3
        var sTriangleCoords = floatArrayOf(
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
        )
        var sTextureCoords = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f

        )

    }

    private fun loadShader(type: Int, shaderCode: String?): Int {

        val shader = GLES31.glCreateShader(type)

        GLES31.glShaderSource(shader, shaderCode)
        GLES31.glCompileShader(shader)
        return shader
    }


    init {
        mVertexBufferFirstPass =
            ByteBuffer.allocateDirect(sTriangleCoords.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBufferFirstPass.put(sTriangleCoords).position(0)

        mTextureBufferFirstPass =
            ByteBuffer.allocateDirect(sTextureCoords.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBufferFirstPass.put(sTextureCoords).position(0)


        val vertexShader: Int = loadShader(
            GLES31.GL_VERTEX_SHADER,
            mVertexShaderCodeFirstPass
        )
        val fragmentShader: Int = loadShader(
            GLES31.GL_FRAGMENT_SHADER,
            mFragmentShaderCodeFirstPass
        )

        mFrameBuffer = IntBuffer.allocate(1)
        GLES31.glGenFramebuffers(1, mFrameBuffer)

        mProgramFirstPass = GLES31.glCreateProgram()

        GLES31.glAttachShader(mProgramFirstPass, vertexShader)

        GLES31.glAttachShader(mProgramFirstPass, fragmentShader)

        GLES31.glBindAttribLocation(mProgramFirstPass, 0, "a_Position")
        GLES31.glBindAttribLocation(mProgramFirstPass, 1, "a_TexCoordinate")

        GLES31.glLinkProgram(mProgramFirstPass)

        mTextureDataHandle = loadTexture(mActivityContext, R.drawable.coffeebeans)
    }

    init {

        mVertexBufferSecondPass =
            ByteBuffer.allocateDirect(sTriangleCoords.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBufferSecondPass.put(sTriangleCoords).position(0)

        mTextureBufferSecondPass =
            ByteBuffer.allocateDirect(sTextureCoords.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBufferSecondPass.put(sTextureCoords).position(0)


        val vertexShader: Int = loadShader(
            GLES31.GL_VERTEX_SHADER,
            mVertexShaderCodeSecondPass
        )
        val fragmentShader: Int = loadShader(
            GLES31.GL_FRAGMENT_SHADER,
            mFragmentShaderCodeSecondPass
        )



        mProgramSecondPass = GLES31.glCreateProgram()

        GLES31.glAttachShader(mProgramSecondPass, vertexShader)

        GLES31.glAttachShader(mProgramSecondPass, fragmentShader)

        GLES31.glBindAttribLocation(mProgramSecondPass, 0, "a_Position")
        GLES31.glBindAttribLocation(mProgramSecondPass, 1, "a_TexCoordinate")

        GLES31.glLinkProgram(mProgramSecondPass)



    }
}