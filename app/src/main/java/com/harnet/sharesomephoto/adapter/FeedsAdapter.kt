package com.harnet.sharesomephoto.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemImageBinding
import com.harnet.sharesomephoto.model.Image
import com.harnet.sharesomephoto.model.ImageParsable
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage

class FeedsAdapter(private var imagesList: ArrayList<Image>) :
    RecyclerView.Adapter<FeedsAdapter.ImagesViewHolder>(), ImageParsable {

    //for updating information from a backend
    fun updateFeedsList(newImagesList: List<Image>) {
        // get new parsed articles
        if (newImagesList.isNotEmpty()) {
            val newCastedImagesList = newImagesList as ArrayList<Image>

            imagesList.clear()
            imagesList.addAll(newCastedImagesList)
            //reset RecycleView and recreate a list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsAdapter.ImagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemImageBinding>(
            inflater,
            R.layout.item_image,
            parent,
            false
        )
        return ImagesViewHolder(view)
    }

    class ImagesViewHolder(var view: ItemImageBinding): RecyclerView.ViewHolder(view.root)

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: FeedsAdapter.ImagesViewHolder, position: Int) {
        // bind data to xml variable view
        holder.view.image = imagesList[position]
//        Log.i("FeedsImages", imagesList[position].url)
//        holder.view.imageItemImage.loadImage(imagesList[position].url, getProgressDrawable(holder.view.imageItemImage.context))
        //TODO change User model to keep user Image as well
//        getProfileImgByUser(imagesList[position].name, holder.view.userImageImageViewProfile)
    }

}