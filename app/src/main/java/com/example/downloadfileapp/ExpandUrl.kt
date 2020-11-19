package com.example.downloadfileapp

import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL

class ExpandUrl : AsyncTask<String?, String?, String?>() {
    var shortenedUrl: String? = null
    private var expandedURL: String? = null


    override fun onPostExecute(result: String?) {
        if (result == null || result === "") {
            MainActivity.mainActivity?.expandedURL = shortenedUrl!!
            MainActivity.mainActivity?.downLoadRequest(shortenedUrl)
        } else {
            MainActivity.mainActivity?.expandedURL = result
            MainActivity.mainActivity?.downLoadRequest(result)
        }
    }

    override fun doInBackground(vararg params: String?): String? {
        shortenedUrl = params[0]
        // open connection
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url = URL(params[0])
            httpURLConnection = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
            // stop following browser redirect
            httpURLConnection.instanceFollowRedirects = false

            // extract location header containing the actual destination URL
            expandedURL = httpURLConnection!!.getHeaderField("Location")
            httpURLConnection.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
            expandedURL = null
        }
        return expandedURL
    }
}