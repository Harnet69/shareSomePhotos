package com.harnet.sharesomephoto.view

import android.graphics.Bitmap
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
import com.harnet.sharesomephoto.util.setActivityTitle
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
        this.setActivityTitle("Image preview")
        var imageBtm: Bitmap? = null
        var isProfileImg: Boolean? = null
        viewModel = ViewModelProvider(this).get(ImagePreviewViewModel::class.java)

        arguments?.let {
            imageBtm = ImagePreviewFragmentArgs.fromBundle(it).image
            isProfileImg = ImagePreviewFragmentArgs.fromBundle(it).isProfilesImg
            image_ImagePreviewFragment.setImageBitmap(imageBtm)
        }

        shareBtn_ImagePreviewFragment.setOnClickListener {
            imageBtm?.let { it1 -> isProfileImg?.let { it2 ->
//                viewModel.makeImgNotProfiles()
                viewModel.sendImageToParseServer(context, it1,
                    it2, image_ImagePreviewFragment)
            } }
        }
    }
}