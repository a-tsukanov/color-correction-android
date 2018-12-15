package poms.edu.colorcorrectionclient.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import poms.edu.colorcorrectionclient.R
import kotlinx.android.synthetic.main.item_filter.view.*
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import java.lang.Exception


class FiltersAdapter(
    private val items: List<String>,
    private val clickListener: (Int) -> Unit)
    : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter, parent, false) as LinearLayout
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        fun downloadAndDisplayImage() {
            with(holder.linearLayout) {
                filter_name.text = currentItem
                val url = ColorCorrectionHttpClient
                    .getAbsoluteUrl("get_filter_img_by_name?name=$currentItem")
                Picasso
                    .get()
                    .load(url)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(filter_item_image, object : Callback {
                        override fun onError(e: Exception?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onSuccess() {
                            filter_img_progress.visibility = View.GONE
                        }

                    })

                setOnClickListener {
                    clickListener(position)
                }
            }
        }
        downloadAndDisplayImage()
    }

    inner class ViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)
}