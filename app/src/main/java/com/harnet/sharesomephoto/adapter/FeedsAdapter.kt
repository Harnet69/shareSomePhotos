package com.harnet.sharesomephoto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemFeedBinding
import com.harnet.sharesomephoto.model.Image

class FeedsAdapter(private var imagesList: ArrayList<Image>) :
    RecyclerView.Adapter<FeedsAdapter.ImagesViewHolder>() {

    //for updating information from a backend
    fun updateFeedsList(newImagesList: ArrayList<Image>) {
        if(newImagesList.isNotEmpty()) {
            // get new parsed articles
            imagesList.clear()
            imagesList.addAll(newImagesList)
            //reset RecycleView and recreate a list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsAdapter.ImagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemFeedBinding>(
            inflater,
            R.layout.item_feed,
            parent,
            false
        )
        return ImagesViewHolder(view)
    }

    class ImagesViewHolder(var view: ItemFeedBinding): RecyclerView.ViewHolder(view.root)

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: FeedsAdapter.ImagesViewHolder, position: Int) {
        // bind data to xml variable view
        holder.view.image = imagesList[position]
    }

    // fix blinking RecyclerView
    override fun getItemId(position: Int): Long {
        return imagesList.get(position).imageId.toLong()
    }
}