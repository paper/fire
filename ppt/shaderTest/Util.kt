package com.shaderTest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.util.Log

class Util {

    companion object {

        lateinit var context: Context

        fun decodeBitmapFromAssets(filename : String) : Bitmap {
            val options = BitmapFactory.Options()
            options.inSampleSize = 1
            val bitmap = BitmapFactory.decodeStream(context.assets.open(filename))
            if (bitmap == null) {
                Log.e("debug", "bitmap decode fail, path = $filename")
            }
            return bitmap
        }


    }



}