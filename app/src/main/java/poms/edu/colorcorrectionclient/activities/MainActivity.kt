package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndDoOnSuccess
import poms.edu.colorcorrectionclient.network.parseFilterNames
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.imageBitmap
import kotlin.math.roundToInt

class MainActivity : Activity(),
    ImageFragment.OnButtonPressedListener,
    FiltersFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPickImageButtonPressed() {
        pickImageFromGallery()
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val inputStream = contentResolver.openInputStream(data!!.data)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val imgContainer = fragmentManager
                .findFragmentById(R.id.image_fragment_container)
                .view

            val imgView: ImageView = imgContainer.main_image

            fun min(a: Float, b: Float) = if (a < b) a else b

            val ratio = min(
                imgContainer.height.toFloat() / bitmap.height.toFloat(),
                imgContainer.width.toFloat() / bitmap.width.toFloat()
            )

            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).roundToInt(),
                (bitmap.height * ratio).roundToInt(),
                true
            )

            imgView.imageBitmap = scaledBitmap
        }
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }


}
