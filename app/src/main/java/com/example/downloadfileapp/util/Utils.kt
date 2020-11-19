package com.example.downloadfileapp.util

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import java.util.*

object Utils {
    //String Values to be Used in App
    const val downloadDirectory = "Androhub Downloads"
    const val mainUrl = "http://androhub.com/demo/"
    const val downloadPdfUrl = "http://androhub.com/demo/demo.pdf"
    const val downloadDocUrl = "http://androhub.com/demo/demo.doc"
    const val downloadZipUrl = "http://androhub.com/demo/demo.zip"
    const val downloadVideoUrl = "https://d2gjspw5enfim.cloudfront.net/qot_web/tignum_x_video.mp4"
    const val downloadMp3Url = "http://androhub.com/demo/demo.mp3"
    @JvmStatic
    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(context.applicationContext,
                    null)[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    @JvmStatic
    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }
}