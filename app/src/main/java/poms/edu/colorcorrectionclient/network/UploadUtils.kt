/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.network

import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

fun uploadImageAndThen(imgFile: File, andThen: (String) -> Unit) {
    val url = ColorCorrectionHttpClient.getAbsoluteUrl("" +
            "send_image")


    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", "a",
            RequestBody.create(MediaType.parse("image/jpg"), imgFile)
        )
        .build()

    ColorCorrectionHttpClient.post(
        url,
        requestBody,
        object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = response.body()?.string()
                val imageToken = JSONObject(responseJson).getString("image_token")
                andThen(imageToken)
            }

        }
    )
}