package com.example.downloadfileapp.util

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.downloadfileapp.FileDownloaderApp
import com.example.downloadfileapp.MainActivity
import com.example.downloadfileapp.database.DownloadedFileEntity
import java.net.MalformedURLException
import java.net.URL

open class MainActivityUtility {
    fun checkPermission(applicationContext: FileDownloaderApp): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            MainActivity.mainActivity!!,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            MainActivity.PERMISSION_REQUEST_CODE
        )
    }


    fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(MainActivity.mainActivity!!)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    fun checkFileAlreadyDownloaded(
        url: String,
        fileEntityList: List<DownloadedFileEntity?>?
    ): Boolean {
        var flag = false
        if (null != fileEntityList) {
            for (i in fileEntityList!!.indices) {
                if (fileEntityList!![i]!!.shortenedURL == url) {
                    flag = true
                    break
                }
            }
        } else {
            flag = true
        }
        return flag
    }

    fun getFileNameFromURL(url: String?): String {
        if (url == null) {
            return ""
        }
        try {
            val resource = URL(url)
            val host = resource.host
            if (host.length > 0 && url.endsWith(host)) {
                // handle ...example.com
                return ""
            }
        } catch (e: MalformedURLException) {
            return ""
        }
        val startIndex = url.lastIndexOf('/') + 1
        val length = url.length

        // find end index for ?
        var lastQMPos = url.lastIndexOf('?')
        if (lastQMPos == -1) {
            lastQMPos = length
        }

        // find end index for #
        var lastHashPos = url.lastIndexOf('#')
        if (lastHashPos == -1) {
            lastHashPos = length
        }

        // calculate the end index
        val endIndex = Math.min(lastQMPos, lastHashPos)
        return url.substring(startIndex, endIndex)
    }

}