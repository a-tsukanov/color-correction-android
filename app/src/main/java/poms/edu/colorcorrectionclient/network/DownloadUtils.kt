package poms.edu.colorcorrectionclient.network

import android.content.Context
import android.graphics.Bitmap
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.File
import java.io.IOException

fun parseFilterNames(response: Response): List<String> {
        val json = JSONArray(response.body()!!.string())
        return List(json.length()) {
            json[it].toString()
        }
    }

fun downloadFilterIconByNameAsyncAndDoOnSuccess(
        context: Context,
        name: String) {

//    ColorCorrectionHttpClient.get(
//        ColorCorrectionHttpClient.getAbsoluteUrl("get_filter_img"),
//        RequestParams(mapOf("name" to name)),
//        object: FileAsyncHttpResponseHandler(context) {
//            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, file: File?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: File?) {
//                onSuccessAction(statusCode, headers, response)
//            }
//        }
//    )
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

fun getImgFromResponse(file: File): Bitmap {
    TODO("not implemented")
}
