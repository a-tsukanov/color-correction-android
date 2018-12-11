package poms.edu.colorcorrectionclient

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textView
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.verticalLayout

class MainActivity : Activity(),
    ImageFragment.OnFragmentInteractionListener,
    FiltersFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doAsync {
            val items = loadFilterItems()
            uiThread {
                setContentView(R.layout.activity_main)
                val frag: FiltersFragment =
                    FiltersFragment.newInstance(items)
                fragmentManager
                    .beginTransaction()
                    .replace(R.id.filters_fragment_container, frag)
                    .commit()
            }
        }
    }
}
