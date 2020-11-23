package com.harnet.sharesomephoto.service.permissions

import android.Manifest
import android.app.Activity
import androidx.fragment.app.Fragment

class ImagePermissionService(activity: Activity, fragment: Fragment) : PermissionService(activity, fragment) {
    override val permissionCode: Int = 123
    override val permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
    override val rationaleTitle = "Access to image library"
    override val rationaleMessage = "If you want to add image, you should give an access to your image library"
}