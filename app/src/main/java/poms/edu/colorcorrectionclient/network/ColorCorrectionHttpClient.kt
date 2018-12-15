package poms.edu.colorcorrectionclient.network

import okhttp3.*

object ColorCorrectionHttpClient {

    private val client = OkHttpClient()
    private const val BASE_URL = "http://10.0.2.2:5000/"

    public fun getAbsoluteUrl(suffix: String): String = "$BASE_URL$suffix"

    public fun get(
        url: String,
        callback: Callback) {

        val request = Request.Builder()
            .url(url)
            .build()

        client
            .newCall(request)
            .enqueue(callback)
    }

}