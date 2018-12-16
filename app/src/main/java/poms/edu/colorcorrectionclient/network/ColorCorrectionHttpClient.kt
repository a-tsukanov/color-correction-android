/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.network

import okhttp3.*
import java.util.concurrent.TimeUnit

object ColorCorrectionHttpClient {

    private val client = OkHttpClient()
    private val clientWithTimeout = client.newBuilder()
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "http://10.0.2.2:5000/"

    public fun getAbsoluteUrl(suffix: String): String = "$BASE_URL$suffix"

    public fun get(
        url: String,
        callback: Callback,
        withTimeOut: Boolean = false) {

        val request = Request.Builder()
            .url(url)
            .build()

        when(withTimeOut) {
            false -> client
            true -> clientWithTimeout
        }.run {
                newCall(request)
                .enqueue(callback)
        }
    }

    public fun post(
        url: String,
        requestBody: RequestBody,
        callback: Callback) {

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client
            .newCall(request)
            .enqueue(callback)
    }

}