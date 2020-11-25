package com.harnet.sharesomephoto.model

import android.app.Activity
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.service.ImagesService

data class AppPermissions(val activity: Activity,val fragment: Fragment){
    val imagesService: ImagesService = ImagesService(activity, fragment)
}