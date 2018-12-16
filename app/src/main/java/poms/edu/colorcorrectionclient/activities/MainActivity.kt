/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import poms.edu.colorcorrectionclient.images.drawableToFile
import poms.edu.colorcorrectionclient.network.*


class MainActivity : FragmentActivity() {

    private val imageFragment: ImageFragment = ImageFragment()
    private lateinit var filtersFragment: FiltersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openImageFragment()

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

    private fun openImageFragment() {
        openFragmentInsideContainer(imageFragment, R.id.image_fragment_container)
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

    private fun uploadCurrentImageAndGetProcessedImageAndShow(filterName: String) {

        fun downloadImageAndShow(imageToken: String, filterName: String) = runOnUiThread {
            downloadProcessedImage(imageToken, filterName)
                .into(
                    imageFragment.view!!.main_image
                )
        }

        val drawable = imageFragment.mainImageDrawable!!
        val imgFile = drawableToFile(drawable, filesDir)
        uploadImageAndThen(imgFile) { imageToken ->
            downloadImageAndShow(imageToken, filterName)
        }
    }

}
