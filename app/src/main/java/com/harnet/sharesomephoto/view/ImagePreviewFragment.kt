package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harnet.sharesomephoto.viewModel.ImagePreviewViewModel
import com.harnet.sharesomephoto.R

class ImagePreviewFragment : Fragment() {

    companion object {
        fun newInstance() = ImagePreviewFragment()
    }

    private lateinit var viewModel: ImagePreviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_preview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImagePreviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}