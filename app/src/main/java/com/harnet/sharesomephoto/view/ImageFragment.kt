package com.harnet.sharesomephoto.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ImageFragmentBinding
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
import com.harnet.sharesomephoto.viewModel.ImageViewModel
import kotlinx.android.synthetic.main.image_fragment.*

/**
 * Class NOT USED! was created for keeping methods to get user images Uri from the device
 */

class ImageFragment : Fragment() {
    private lateinit var viewModel: ImageViewModel
    private lateinit var dataBinding: ImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {        // DataBinding
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.image_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        viewModel.mImagesLiveData.observe(viewLifecycleOwner, Observer<List<Uri>> { listOfImage ->
            imageWidgetStatus.text = """ Found ${listOfImage.size} Images"""
            //TODO implement image chooser from Array with Images Paths
            for (image in listOfImage) {
                Log.i("userImages", "onViewCreated: $image")
            }
            userImage_imageView.loadImage(
                listOfImage[2].toString(),
                getProgressDrawable(view.context)
            )
        })

        // load images
        viewModel.getAllImages(activity as Activity)

        // open gallery
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        startActivityForResult(intent, 123)

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select image"), 123)
    }
}