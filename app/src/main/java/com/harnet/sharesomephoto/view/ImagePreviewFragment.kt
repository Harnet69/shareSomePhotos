package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.harnet.sharesomephoto.viewModel.ImagePreviewViewModel
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ImagePreviewFragmentBinding
import kotlinx.android.synthetic.main.image_preview_fragment.*

class ImagePreviewFragment : Fragment() {
    private lateinit var viewModel: ImagePreviewViewModel
    private lateinit var dataBinding: ImagePreviewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.image_preview_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImagePreviewViewModel::class.java)

        arguments?.let {
            val imageBtm = ImagePreviewFragmentArgs.fromBundle(it).image
            image_ImagePreviewFragment.setImageBitmap(imageBtm)
        }
    }
}