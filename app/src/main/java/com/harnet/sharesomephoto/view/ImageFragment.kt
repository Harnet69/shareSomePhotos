package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.harnet.sharesomephoto.viewModel.ImageViewModel
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ImageFragmentBinding
import com.harnet.sharesomephoto.model.Image
import com.harnet.sharesomephoto.util.setActivityTitle

class ImageFragment : Fragment() {
    private lateinit var viewModel: ImageViewModel
    private lateinit var dataBinding: ImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.image_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setActivityTitle("Image viewer")
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        arguments?.let {
            val imageUrl = ImageFragmentArgs.fromBundle(it).imageUrl
            dataBinding.imageURL =  imageUrl
        }
    }
}