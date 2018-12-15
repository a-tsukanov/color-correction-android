package poms.edu.colorcorrectionclient.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import poms.edu.colorcorrectionclient.R
import kotlinx.android.synthetic.main.item_filter.view.*
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient


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
        with(holder.linearLayout) {
            filter_name.text = currentItem
            val url = ColorCorrectionHttpClient
                .getAbsoluteUrl("get_filter_img_by_name?name=$currentItem")
            Picasso
                .get()
                .load(url)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(filter_item_image)

            setOnClickListener {
                clickListener(position)
            }
        }
    }


    inner class ViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)
}