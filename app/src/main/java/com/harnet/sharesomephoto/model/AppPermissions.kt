package com.harnet.sharesomephoto.model

import android.app.Activity
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.service.ImagesPermissionService

data class AppPermissions(val activity: Activity,val fragment: Fragment){
    val imagesPermissionService: ImagesPermissionService = ImagesPermissionService(activity, fragment)
}