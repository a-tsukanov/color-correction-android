package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndDoOnSuccess
import poms.edu.colorcorrectionclient.network.parseFilterNames
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import poms.edu.colorcorrectionclient.images.getScaledBitmapForContainer
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import java.io.IOException
import kotlin.math.roundToInt

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAndOpenImageFragment()

        downloadFilterNamesAsyncAndDoOnSuccess { _, response ->
            val itemNames = parseFilterNames(response)

            hideProgressBar()
            showFiltersInNewFragment(itemNames)

        }
    }

    private fun createAndOpenImageFragment() {
        val imgFrag = ImageFragment.newInstance(onButtonPressedCallback = ::pickImageFromGallery)
        fragmentManager
            .beginTransaction()
            .replace(R.id.image_fragment_container, imgFrag)
            .commit()
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun showFiltersInNewFragment(items: List<String>) {

        val frag: FiltersFragment =
            FiltersFragment.newInstance(items, ::getFilterInfo)
        fragmentManager
            .beginTransaction()
            .replace(R.id.filters_fragment_container, frag)
            .commit()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fun retrieveImage(): Bitmap {
            val inputStream = contentResolver.openInputStream(data!!.data)
            return BitmapFactory.decodeStream(inputStream)
        }

        if (requestCode != REQUEST_PICK_IMAGE || resultCode != Activity.RESULT_OK)
            return

        val bitmap = retrieveImage()

        val imgContainer = fragmentManager
            .findFragmentById(R.id.image_fragment_container)
            .view

        val scaledBitmap = getScaledBitmapForContainer(bitmap, imgContainer)

        imgContainer
            .main_image
            .imageBitmap = scaledBitmap

    }

    private fun getFilterInfo(itemName: String) {
        toast(itemName)
        val url = ColorCorrectionHttpClient.getAbsoluteUrl(
            "get_grid_by_name?name=$itemName"
        )
        ColorCorrectionHttpClient.get(url, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call, response: Response) {
                val contents = response.body()!!.string()
                doAsync {
                    uiThread {
                        toast(contents)
                    }
                }
            }

        })
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }


}
