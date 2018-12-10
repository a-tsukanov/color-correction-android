package poms.edu.colorcorrectionclient

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class MainActivity : Activity(),
    ImageFragment.OnFragmentInteractionListener,
    FiltersFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


    }
}
