package com.manwinder.spinningsquare.utils

import org.json.JSONObject
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Could use retrofit but the request response very small, no need to increase apk size
 */
class NetworkUtils {
    companion object {
        @Throws(IOException::class, JSONException::class)
        fun getJSONObjectFromURL(urlString: String): JSONObject {
            val urlConnection: HttpURLConnection?
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 2000
            urlConnection.connectTimeout = 5000
            urlConnection.doOutput = true
            urlConnection.connect()

            val br = BufferedReader(InputStreamReader(url.openStream()))
            val sb = StringBuilder()

            var line: String? = br.readLine()
            while (line != null) {
                sb.append(line + "\n")
                line = br.readLine()
            }

            br.close()

            val jsonString = sb.toString()

            return JSONObject(jsonString)
        }
    }
}