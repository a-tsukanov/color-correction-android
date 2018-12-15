package poms.edu.colorcorrectionclient.network

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
