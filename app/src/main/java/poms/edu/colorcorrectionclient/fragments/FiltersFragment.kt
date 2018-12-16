/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_filters.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import poms.edu.colorcorrectionclient.adapters.FiltersAdapter
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import java.io.IOException


class FiltersFragment : Fragment() {
    private var onFilterChosenCallback: ((String) -> Unit)? = null
    private lateinit var itemNames: List<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_filters, container, false)
        layout.recycler_view.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayout.HORIZONTAL,
                false
            )
            adapter = FiltersAdapter(itemNames) { item -> onFilterChosenCallback?.invoke(item) }
        }
        return layout
    }

    override fun onDetach() {
        super.onDetach()
        onFilterChosenCallback = null
    }

    companion object {
        fun newInstance(
            items: List<String>,
            onFilterChosenCallback: (String) -> Unit)
                : FiltersFragment =

                FiltersFragment().apply {
                    itemNames = items
                    this.onFilterChosenCallback = onFilterChosenCallback
                }
    }
}
