package poms.edu.colorcorrectionclient.network

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

object ColorCorrectionHttpClient {

    private val client = AsyncHttpClient()
    private val BASE_URL = "http://10.0.2.2:5000/"

    public fun getAbsoluteUrl(suffix: String): String = "$BASE_URL$suffix"

    public fun get(
        url: String,
        params: RequestParams,
        responseHandler: AsyncHttpResponseHandler) {

        client.get(url, params, responseHandler)
    }

    public fun post(
        url: String,
        params: RequestParams,
        responseHandler: AsyncHttpResponseHandler) {

        client.post(url, params, responseHandler)
    }
}