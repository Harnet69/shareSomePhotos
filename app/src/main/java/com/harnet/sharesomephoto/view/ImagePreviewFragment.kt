package com.harnet.sharesomephoto.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ImagePreviewFragmentBinding
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.ImagePreviewViewModel
import kotlinx.android.synthetic.main.image_preview_fragment.*

class ImagePreviewFragment : Fragment() {
    private lateinit var viewModel: ImagePreviewViewModel
    private lateinit var dataBinding: ImagePreviewFragmentBinding

    private var fromFragment = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.image_preview_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setActivityTitle("Image preview")
        var imageBtm: Bitmap? = null
        viewModel = ViewModelProvider(this).get(ImagePreviewViewModel::class.java)

        arguments?.let {
            //refresh image
            fromFragment = ImagePreviewFragmentArgs.fromBundle(it).fromFragment
            ImagePreviewFragmentArgs.fromBundle(it).image.also { imageBtm = it }
            imageBtm?.let { viewModel.refresh(imageBtm as Bitmap) }
        }

        shareBtn_ImagePreviewFragment.setOnClickListener { btn ->
            imageBtm?.let {
                loadingProgressBar_ImagePreview.visibility = View.VISIBLE
                action_block_ImagePreviewFragment.visibility = View.GONE
                when (fromFragment) {
                    "profile" -> {
                        viewModel.sendImgToParseServer(sendError_ImagePreview, loadingProgressBar_ImagePreview, it, true)
                    }
                    "feeds" -> {
                        viewModel.sendImgToParseServer(sendError_ImagePreview,loadingProgressBar_ImagePreview, it, false)
                    }
                    "userDetails" -> {
                        viewModel.sendImgToParseServer(sendError_ImagePreview,loadingProgressBar_ImagePreview,  it, false)
                    }
                }
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mImage.observe(viewLifecycleOwner, Observer { image ->
            image?.let {
                image_preview_block.visibility = View.VISIBLE
                // set new added image
                image_ImagePreviewFragment.setImageBitmap(image)
            }
        })

        // make error TextViewVisible
        viewModel.mIsLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                loadError_ImagePreview.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // redirect to Profile fragment when image was sent
        viewModel.mIsImageSent.observe(viewLifecycleOwner, Observer { isSent ->
            // check isError not null
            isSent?.let {
                if (it) {
                    loadingProgressBar_ImagePreview.visibility = View.INVISIBLE
                    sendError_ImagePreview.visibility = View.GONE
                }
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingProgressBar_ImagePreview.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    loadError_ImagePreview.visibility = View.GONE
                    image_preview_block.visibility = View.GONE
                }
            }
        })
    }
}