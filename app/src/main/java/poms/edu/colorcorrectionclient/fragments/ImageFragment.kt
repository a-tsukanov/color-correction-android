/*
 * Copyright (c) 2019 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.sdk27.coroutines.onClick
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.images.getScaledBitmapForContainer


class ImageFragment : Fragment() {

    public lateinit var drawableNotProcessed: Drawable
    public var imageToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_image, container, false)
        with(layout) {
            btn_open.onClick {
                pickImageFromGallery()
            }
            btn_disable.onClick {
                main_image.image = drawableNotProcessed
            }
            drawableNotProcessed = main_image!!.drawable
        }
        return layout
    }

    public val currentDrawable: Drawable?
    get() = view
        ?.main_image
        ?.drawable

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        fun retrieveImage(): Bitmap {
            val inputStream = activity!!.contentResolver.openInputStream(data!!.data)
            return BitmapFactory.decodeStream(inputStream)
        }

        if (requestCode != REQUEST_PICK_IMAGE || resultCode != Activity.RESULT_OK)
            return

        val bitmap = retrieveImage()
        scaleAndShowChosenImage(bitmap)
        drawableNotProcessed = currentDrawable!!
        imageToken = null
    }

    private fun scaleAndShowChosenImage(bitmap: Bitmap) {

        val imgContainer = view!!
        val scaledBitmap = getScaledBitmapForContainer(bitmap, imgContainer)

        imgContainer
            .main_image
            .imageBitmap = scaledBitmap
    }

    fun hideProgressBar() {
        view!!.main_image_progress_bar.visibility = View.GONE
    }


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }
}
