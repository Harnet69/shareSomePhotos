package com.harnet.sharesomephoto.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.viewModel.ImageViewModel
import kotlinx.android.synthetic.main.image_fragment.*

class ImageFragment : Fragment() {
    private lateinit var viewModel: ImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        viewModel.getImageList().observe(viewLifecycleOwner, Observer<List<String>> { listOfImage ->
            imageWidgetStatus.text = """ Found ${listOfImage.size} Images"""
            //TODO here is the Array with Images Paths
            for (image in listOfImage) {
                Log.i("userImages", "onViewCreated: $image")
            }
        })

        // load images
        viewModel.getAllImages()
    }
}