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
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import okhttp3.*
import org.jetbrains.anko.*
import org.json.JSONObject
import poms.edu.colorcorrectionclient.images.getScaledBitmapForContainer
import poms.edu.colorcorrectionclient.network.*
import java.io.*


class MainActivity : Activity() {

    private lateinit var imageFragment: ImageFragment
    private lateinit var filtersFragment: FiltersFragment

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
        imageFragment = ImageFragment.newInstance(onButtonPressedCallback = ::pickImageFromGallery)
        fragmentManager
            .beginTransaction()
            .replace(R.id.image_fragment_container, imageFragment)
            .commit()
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun showFiltersInNewFragment(items: List<String>) {

        filtersFragment =
            FiltersFragment.newInstance(items, ::requestForApplyFilter)
        fragmentManager
            .beginTransaction()
            .replace(R.id.filters_fragment_container, filtersFragment)
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

        val imgContainer = imageFragment.view

        val scaledBitmap = getScaledBitmapForContainer(bitmap, imgContainer)

        imgContainer
            .main_image
            .imageBitmap = scaledBitmap

    }

    private fun getImgFile(): File {
        val img = imageFragment.view.main_image.image
        val imgBitmap = when(img) {
            is BitmapDrawable -> img.bitmap
            else -> throw NotImplementedError()
        }

        val tmpFile = File(filesDir, "tmp")
        val outputStream = BufferedOutputStream(FileOutputStream(tmpFile))
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return tmpFile
    }



    private fun requestForApplyFilter(filterName: String) {
        val imgFile = getImgFile()
        uploadImageAndThen(imgFile) { imageToken ->
            runOnUiThread {
                downloadProcessedImage(imageToken, filterName)
                    .into(
                        imageFragment.view.main_image
                    )
            }

        }

    }


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }


}
