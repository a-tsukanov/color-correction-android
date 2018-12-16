/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.fragments

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import poms.edu.colorcorrectionclient.R


class ImageFragment : Fragment() {

    private var onButtonPressedCallback: (() -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(
            R.layout.fragment_image, container, false
        )
        with(layout) {
            main_image.onClick {
                onButtonPressedCallback?.invoke()
            }
        }
        return layout
    }

    override fun onDetach() {
        super.onDetach()
        onButtonPressedCallback = null
    }


    companion object {

        fun newInstance(onButtonPressedCallback: () -> Unit) =
            ImageFragment().apply {
                this.onButtonPressedCallback = onButtonPressedCallback
            }
    }
}
