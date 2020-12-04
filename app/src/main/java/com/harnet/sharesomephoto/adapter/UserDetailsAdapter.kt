package com.harnet.sharesomephoto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemGalleryImageBinding
import com.harnet.sharesomephoto.model.Image

class UserDetailsAdapter(private var userImages: ArrayList<Image>) :
    RecyclerView.Adapter<UserDetailsAdapter.UserImagesViewHolder>() {

    //for updating information from a backend
    fun updateFeedsList(newImagesList: ArrayList<Image>) {
        if(newImagesList.isNotEmpty()) {
            // get new parsed articles
            userImages.clear()
            userImages.addAll(newImagesList)
            //reset RecycleView and recreate a list
            notifyDataSetChanged()
        }
    }

    class UserImagesViewHolder(var view: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserImagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemGalleryImageBinding>(
            inflater,
            R.layout.item_gallery_image,
            parent,
            false
        )
        return UserImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserImagesViewHolder, position: Int) {
        // bind data to xml variable view
        holder.view.image = userImages[position]
    }

    override fun getItemCount(): Int {
        return userImages.size
    }
}