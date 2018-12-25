/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.images.drawableToFile
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndThen
import poms.edu.colorcorrectionclient.network.downloadProcessedImage
import poms.edu.colorcorrectionclient.network.parseFilterNames
import poms.edu.colorcorrectionclient.network.uploadImageAndThen


class MainActivity : FragmentActivity() {

    private val imageFragment: ImageFragment = ImageFragment()
    private lateinit var filtersFragment: FiltersFragment

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        if (item?.itemId == R.id.gotoserver_address) {
            startActivity(Intent(this, SetupServerActivity::class.java))
            true
        } else super.onOptionsItemSelected(item)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openImageFragment()

        try {

            downloadFilterNamesAsyncAndThen(
                onSuccessAction = { _, response ->
                    runOnUiThread {
                        val itemNames = parseFilterNames(response)

                        hideProgressBar()
                        createAndOpenFiltersFragment(itemNames)
                    }
                },
                onErrorAction = { _, e ->
                    runOnUiThread {
                        hideProgressBar()
                        longToast("Something went wrong: ${e.message}")
                    }
                })
        }
        catch (e: Throwable) {
            hideProgressBar()
            toast("Oops! Something went wrong.")
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
            .newInstance(items, onFilterChosenCallback = ::getPrecessedImageIntoImageView)
            .also {
                openFragmentInsideContainer(it, R.id.filters_fragment_container)
            }
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun downloadImageAndShow(imageToken: String, filterName: String) = runOnUiThread {

        downloadProcessedImage(imageToken, filterName)
            .placeholder(imageFragment.currentDrawable!!)
            .into(
                imageFragment.view!!.main_image, object: Callback {
                    override fun onSuccess() = runOnUiThread {
                        imageFragment.hideProgressBar()
                    }

                    override fun onError(e: Exception?) = runOnUiThread {
                        imageFragment.hideProgressBar()
                        longToast("Something went wrong")
                    }

                }
            )
    }

    private fun getPrecessedImageIntoImageView(filterName: String) = with(imageFragment.view!!) {


        main_image_progress_bar.visibility = View.VISIBLE

        imageFragment.imageToken?.run {
            downloadImageAndShow(this, filterName)
            return@with
        }

        toast("Uploading your image...")

        val drawable = imageFragment.drawableNotProcessed
        val imgFile = drawableToFile(drawable, filesDir)
        uploadImageAndThen(imgFile,
            onSuccess =  { imageToken ->
                imageFragment.imageToken = imageToken
                runOnUiThread {
                    toast("Getting processed image...")
                }
                downloadImageAndShow(imageToken, filterName)
            },
            onError = {
                runOnUiThread {
                    imageFragment.hideProgressBar()
                    longToast("Something went wrong")
                }
            }
        )
    }

}
