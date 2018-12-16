/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.network

import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.fragment_image.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

fun parseFilterNames(response: Response): List<String> {
        val json = JSONArray(response.body()!!.string())
        return List(json.length()) {
            json[it].toString()
        }
    }


fun downloadFilterNamesAsyncAndDoOnSuccess(onSuccessAction: (Call, Response) -> Unit) {

    ColorCorrectionHttpClient.get(
        ColorCorrectionHttpClient.getAbsoluteUrl("get_all_filters"),
        object: Callback {
            override fun onResponse(call: Call, response: Response) {
                onSuccessAction(call, response)
            }

            override fun onFailure(call: Call, e: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    )
}

fun downloadProcessedImage(imageToken: String, filterName: String): RequestCreator {
    val url = ColorCorrectionHttpClient.getAbsoluteUrl(
        "process_image?image_token=$imageToken&grid_name=$filterName")

    return Picasso
        .get()
        .load(url)
        .networkPolicy(NetworkPolicy.NO_CACHE)
        .memoryPolicy(MemoryPolicy.NO_CACHE)
}
