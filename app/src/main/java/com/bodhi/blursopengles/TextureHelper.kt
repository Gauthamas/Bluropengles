package com.bodhi.blursopengles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES31
import android.opengl.GLUtils

// Thanks to https://github.com/learnopengles/Learn-OpenGLES-Tutorials/blob/master/android/AndroidOpenGLESLessons/app/src/main/java/com/learnopengles/android/common/TextureHelper.java

object TextureHelper {


    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES31.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error")
        }
        val options = BitmapFactory.Options()
        options.inScaled = true

        var bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        bitmap = bitmap.copy( Bitmap.Config.ARGB_8888 , true);

        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureHandle[0])

        GLES31.glTexParameteri(
            GLES31.GL_TEXTURE_2D,
            GLES31.GL_TEXTURE_MIN_FILTER,
            GLES31.GL_NEAREST
        )
        GLES31.glTexParameteri(
            GLES31.GL_TEXTURE_2D,
            GLES31.GL_TEXTURE_MAG_FILTER,
            GLES31.GL_NEAREST
        )

        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S,
            GLES31.GL_CLAMP_TO_EDGE)

        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T,
            GLES31.GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GLES31.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()
        return textureHandle[0]
    }

    fun configureTextureOutput(width:Int, height:Int): Int{
        var textureHandle = IntArray(1)
        GLES31.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureHandle[0])

        // Set filtering
        GLES31.glTexParameteri(
            GLES31.GL_TEXTURE_2D,
            GLES31.GL_TEXTURE_MIN_FILTER,
            GLES31.GL_NEAREST
        )
        GLES31.glTexParameteri(
            GLES31.GL_TEXTURE_2D,
            GLES31.GL_TEXTURE_MAG_FILTER,
            GLES31.GL_NEAREST
        )
        GLES31.glTexImage2D(
            GLES31.GL_TEXTURE_2D,
            0,
            GLES31.GL_RGBA,
            width,
            height,
            0,
            GLES31.GL_RGBA,
            GLES31.GL_UNSIGNED_BYTE,
            null
        )

        return textureHandle[0]
    }
}