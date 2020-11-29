package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.harnet.sharesomephoto.model.ImageParsable

class ImagePreviewViewModel(application: Application) : BaseViewModel(application), ImageParsable {

    fun sendImageToParseServer(context: Context?,
                               chosenImage: Bitmap,
                               isProfileImage: Boolean,
                               profileImageView: ImageView?){
        sendImgToParseServer(context, chosenImage, isProfileImage, profileImageView)
    }

}