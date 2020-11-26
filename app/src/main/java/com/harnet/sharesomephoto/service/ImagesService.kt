package com.harnet.sharesomephoto.service

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.ImageParsable
import com.harnet.sharesomephoto.model.UserParsable
import com.harnet.sharesomephoto.view.FeedsFragment
import com.harnet.sharesomephoto.view.ProfileFragment
import kotlinx.android.synthetic.main.profile_fragment.*

class ImagesService(activity: Activity, fragment: Fragment) :
    PermissionService(activity, fragment), ImageParsable {
    override val permissionCode: Int =
        activity.resources.getString(R.string.permissionImagesCode).toInt()
    override val permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
    override val rationaleTitle = "Access to image library"
    override val rationaleMessage =
        "If you want to add image, you should give an access to your image library"

    //get image from Image Library of device
    private fun getImageFromLibrary(data: Intent?): Bitmap? {
        val selectedImage = data?.data
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    // handle with image permission
    fun handleWithImageFromLib(navFragment: Fragment, data: Intent?) {
        val bitmap = getImageFromLibrary(data)
        //check what fragment is current
        when (val activeFragment: Fragment? =
            navFragment.childFragmentManager.primaryNavigationFragment) {
            is ProfileFragment -> {
//                navFragment.userImage_ImageView_Profile.setImageBitmap(bitmap)
                //TODO record this image to User account on Parse server
                bitmap?.let {
                    sendImageToParseServer(activeFragment.context, bitmap, true, navFragment.userImage_ImageView_Profile)
                }
            }
            is FeedsFragment -> {
                Toast.makeText(activeFragment.context, "Feeds fragment", Toast.LENGTH_LONG).show()
                //TODO implement method for an users fragment
                bitmap?.let {
                    sendImageToParseServer(activeFragment.context, bitmap, false, null)
                }
            }
        }
    }
}