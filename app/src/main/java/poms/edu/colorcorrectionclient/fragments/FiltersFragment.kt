/*
 * Copyright (c) 2019 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_filters.view.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.longToast
import poms.edu.colorcorrectionclient.adapters.FiltersAdapter
import poms.edu.colorcorrectionclient.R


class FiltersFragment : Fragment() {

    private var onFilterChosenCallback: ((String) -> Unit)? = null
    private var onSaveClickedCallback: (() -> Unit)? = null
    private lateinit var filterNames: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_filters, container, false)
        with(layout.recycler_view) {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayout.HORIZONTAL,
                false
            )
            adapter = FiltersAdapter(filterNames) { item -> onFilterChosenCallback?.invoke(item) }
        }
        with(layout) {
            btn_open.onClick {
                pickImageFromGallery()
            }
            btn_save.onClick {
                onSaveClickedCallback?.invoke()
            }
        }
        return layout
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        activity?.startActivityForResult(intent, FiltersFragment.REQUEST_PICK_IMAGE)
    }

    override fun onDetach() {
        super.onDetach()
        onFilterChosenCallback = null
    }

    companion object {
        fun newInstance(
            filterNames: List<String>,
            onFilterChosenCallback: (String) -> Unit,
            onSaveClickedCallback: () -> Unit
        )
                : FiltersFragment =

                FiltersFragment().apply {
                    this.filterNames = filterNames
                    this.onFilterChosenCallback = onFilterChosenCallback
                    this.onSaveClickedCallback = onSaveClickedCallback
                }

        const val REQUEST_PICK_IMAGE = 1

    }
}
