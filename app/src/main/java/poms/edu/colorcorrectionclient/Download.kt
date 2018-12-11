package poms.edu.colorcorrectionclient

import org.json.JSONArray
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

private fun parseFilterItems(json: JSONArray): List<FilterItem> =
    List(json.length()) {
        FilterItem(
            name = json[it].toString(),
            image_id = R.mipmap.icon_filter)
    }

