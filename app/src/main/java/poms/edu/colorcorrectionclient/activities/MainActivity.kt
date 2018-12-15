package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import poms.edu.colorcorrectionclient.network.parseFilterNames

class MainActivity : Activity(),
    ImageFragment.OnFragmentInteractionListener,
    FiltersFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadFilterNamesAsyncAndDoOnSuccess { _, _, response ->

            val items = parseFilterNames(response!!)
            val frag: FiltersFragment =
                FiltersFragment.newInstance(items)
            fragmentManager
                .beginTransaction()
                .replace(R.id.filters_fragment_container, frag)
                .commit()
        }
    }

    private fun downloadFilterNamesAsyncAndDoOnSuccess(
        onSuccessAction: (statusCode: Int, headers: Array<out Header>?, response: JSONArray?) -> Unit) {

        ColorCorrectionHttpClient.get(
            ColorCorrectionHttpClient.getAbsoluteUrl("get_all_filters"),
            RequestParams(),
            object: JsonHttpResponseHandler("utf-8") {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    onSuccessAction(statusCode, headers, response)
                }
            }
        )
    }
}
