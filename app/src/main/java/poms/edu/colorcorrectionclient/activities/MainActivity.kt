/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.*
import poms.edu.colorcorrectionclient.images.drawableToFile
import poms.edu.colorcorrectionclient.images.getScaledBitmapForContainer
import poms.edu.colorcorrectionclient.network.*


class MainActivity : FragmentActivity() {

    private lateinit var imageFragment: ImageFragment
    private lateinit var filtersFragment: FiltersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAndOpenImageFragment()

        downloadFilterNamesAsyncAndDoOnSuccess { _, response ->
            val itemNames = parseFilterNames(response)

            hideProgressBar()
            createAndOpenFiltersFragment(itemNames)

        }
    }

    private fun openFragmentInsideContainer(fragment: Fragment, containerId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    private fun createAndOpenImageFragment() {

        imageFragment = ImageFragment
            .newInstance(onButtonPressedCallback = ::pickImageFromGallery)
            .also {
                openFragmentInsideContainer(it, R.id.image_fragment_container)
            }
    }

    private fun createAndOpenFiltersFragment(items: List<String>) {

        filtersFragment = FiltersFragment
            .newInstance(items, onFilterChosenCallback = ::uploadCurrentImageAndGetProcessedImageAndShow)
            .also {
                openFragmentInsideContainer(it, R.id.filters_fragment_container)
            }
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun scaleAndShowChosenImage(bitmap: Bitmap) {
        val imgContainer = imageFragment.view!!

        val scaledBitmap = getScaledBitmapForContainer(bitmap, imgContainer)

        imgContainer
            .main_image
            .imageBitmap = scaledBitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fun retrieveImage(): Bitmap {
            val inputStream = contentResolver.openInputStream(data!!.data)
            return BitmapFactory.decodeStream(inputStream)
        }

        if (requestCode != REQUEST_PICK_IMAGE || resultCode != Activity.RESULT_OK)
            return

        val bitmap = retrieveImage()
        scaleAndShowChosenImage(bitmap)

    }

    private fun uploadCurrentImageAndGetProcessedImageAndShow(filterName: String) {

        val drawable = imageFragment.mainImageDraweble!!
        val imgFile = drawableToFile(drawable, filesDir)
        uploadImageAndThen(imgFile) { imageToken ->
            runOnUiThread {
                downloadProcessedImage(imageToken, filterName)
                    .into(
                        imageFragment.view!!.main_image
                    )
            }
        }
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }

}
