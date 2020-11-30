package com.harnet.sharesomephoto.service

import android.Manifest
import android.app.Activity
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.ImageParsable

class ImagesService(activity: Activity, fragment: Fragment) :
    PermissionService(activity, fragment){
    override val permissionCode: Int =
        activity.resources.getString(R.string.permissionImagesCode).toInt()
    override val permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
    override val rationaleTitle = "Access to image library"
    override val rationaleMessage = "If you want to add image, you should give an access to your image library"
}