package poms.edu.colorcorrectionclient

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.imageResource

data class FilterItem(val name: String, val image_id: Int)

class FiltersAdapter(private val items: List<FilterItem>)
    : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter, parent, false) as LinearLayout
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current_item = items[position]
        with(holder.linearLayout) {
            findViewById<TextView>(R.id.filter_name).text = current_item.name
            findViewById<ImageView>(R.id.filter_item_image).imageResource = current_item.image_id
        }
    }


    inner class ViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)
}