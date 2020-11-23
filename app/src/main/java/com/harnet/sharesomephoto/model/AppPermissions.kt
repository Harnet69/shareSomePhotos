package com.harnet.sharesomephoto.model

import android.app.Activity
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.service.permissions.ImagePermissionService

data class AppPermissions(val activity: Activity,val fragment: Fragment){
    val imagePermissionService: ImagePermissionService = ImagePermissionService(activity, fragment)
}