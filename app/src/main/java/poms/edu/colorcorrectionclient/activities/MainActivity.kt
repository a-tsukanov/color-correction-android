package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import poms.edu.colorcorrectionclient.network.loadFilterItems
import poms.edu.colorcorrectionclient.network.parseFilterItems

class MainActivity : Activity(),
    ImageFragment.OnFragmentInteractionListener,
    FiltersFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ColorCorrectionHttpClient.get(
            ColorCorrectionHttpClient.getAbsoluteUrl("get_all_filters"),
            RequestParams(),
            object: JsonHttpResponseHandler("utf-8") {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray?) {
                    setContentView(R.layout.activity_main)
                    val items = parseFilterItems(response!!)
                    val frag: FiltersFragment =
                        FiltersFragment.newInstance(items)
                    fragmentManager
                        .beginTransaction()
                        .replace(R.id.filters_fragment_container, frag)
                        .commit()
                }
            }
        )

//        doAsync {
//            val items = loadFilterItems()
//            uiThread {
//
//            }
//        }
    }
}
