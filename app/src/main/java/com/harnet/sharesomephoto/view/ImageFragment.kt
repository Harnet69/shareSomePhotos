package com.harnet.sharesomephoto.view

import android.app.Activity
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
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
import com.harnet.sharesomephoto.viewModel.ImageViewModel
import kotlinx.android.synthetic.main.image_fragment.*

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
                userImage_imageView.loadImage(listOfImage[100].toString(), getProgressDrawable(view.context))
        })

        // load images
        viewModel.getAllImages(activity as Activity)

    }
}