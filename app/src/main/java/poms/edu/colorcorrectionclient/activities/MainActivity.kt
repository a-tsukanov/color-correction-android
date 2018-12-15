package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndDoOnSuccess
import poms.edu.colorcorrectionclient.network.parseFilterNames
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(),
    ImageFragment.OnFragmentInteractionListener,
    FiltersFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadFilterNamesAsyncAndDoOnSuccess { _, response ->
            val itemNames = parseFilterNames(response)

            hideProgressBar()
            showFiltersInNewFragment(itemNames)

        }
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun showFiltersInNewFragment(items: List<String>) {

        val frag: FiltersFragment =
            FiltersFragment.newInstance(items)
        fragmentManager
            .beginTransaction()
            .replace(R.id.filters_fragment_container, frag)
            .commit()
    }


}
