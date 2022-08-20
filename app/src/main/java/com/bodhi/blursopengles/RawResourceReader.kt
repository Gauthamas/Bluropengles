package com.bodhi.blursopengles

// Thanks to https://github.com/learnopengles/Learn-OpenGLES-Tutorials/blob/master/android/AndroidOpenGLESLessons/app/src/main/java/com/learnopengles/android/common/RawResourceReader.java

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object RawResourceReader {
    fun readTextFileFromRawResource(
        context: Context,
        resourceId: Int
    ): String? {
        val inputStream = context.resources.openRawResource(
            resourceId
        )
        val inputStreamReader = InputStreamReader(
            inputStream
        )
        val bufferedReader = BufferedReader(
            inputStreamReader
        )
        var nextLine: String?
        val body = StringBuilder()
        try {
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            return null
        }
        return body.toString()
    }
}
