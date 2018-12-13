package poms.edu.colorcorrectionclient.network

import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.*
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.adapters.FilterItem
import java.net.URL

const val SERVER_IP_AND_PORT = "10.0.2.2:5000"

fun loadFilterItems(): List<FilterItem> =
    loadJson().let {
        parseFilterItems(it)
    }

private fun loadJson(): JSONArray =
    URL("http://$SERVER_IP_AND_PORT/get_all_filters")
        .readText()
        .let {
            JSONArray(it)
        }

fun parseFilterItems(json: JSONArray): List<FilterItem> =
    List(json.length()) {
        FilterItem(
            name = json[it].toString(),
            image_id = R.mipmap.icon_filter
        )
    }

