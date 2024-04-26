package com.wallpaper.mylibrary.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class GetFileFromUri {
    companion object{
        fun getFileFromUri(context: Context, uri: Uri?): File? {
            val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
            val outputFile = File(context.cacheDir, "image.jpg")
            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            return outputFile
        }
    }
}