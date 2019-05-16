/*
 * Copyright (c) 2019 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.toast
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.images.drawableToFile
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndThen
import poms.edu.colorcorrectionclient.network.downloadProcessedImage
import poms.edu.colorcorrectionclient.network.parseFilterNames
import poms.edu.colorcorrectionclient.network.uploadImageAndThen
import java.io.File
import java.util.*


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
            .newInstance(
                items,
                onFilterChosenCallback = ::getProcessedImageIntoImageView,
                onSaveClickedCallback = ::saveImage
            )
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

    private fun getProcessedImageIntoImageView(filterName: String) = with(imageFragment.view!!) {


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        fun retrieveImage(): Bitmap {
            val inputStream = contentResolver.openInputStream(data!!.data)
            return BitmapFactory.decodeStream(inputStream)
        }

        if (requestCode != FiltersFragment.REQUEST_PICK_IMAGE || resultCode != Activity.RESULT_OK)
            return

        val bitmap = retrieveImage()
        imageFragment.scaleAndShowChosenImage(bitmap)
    }

    private fun saveImage() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE
                )
        }
        else {
            saveImageImpl()
        }
    }

    private fun saveImageImpl() {
        val drawable = imageFragment.currentDrawable
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        }
        else throw IllegalArgumentException()

        val name = Calendar.getInstance().time.toString()
        val fullName = "LUT_$name.jpeg"

        MediaStore.Images.Media.insertImage(contentResolver, bitmap, fullName, "")

        longToast("Image was saved")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_WRITE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveImageImpl()
                } else {
                    longToast("Not able to save image without your permission")
                }
                return
            }

            else -> {
            }
        }
    }



    companion object {
        const val PERMISSION_WRITE = 47
    }

}
