package com.harnet.sharesomephoto.service

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.harnet.sharesomephoto.view.FeedsFragment
import com.harnet.sharesomephoto.view.ProfileFragment
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class PermissionService(private val activity: Activity, val fragment: Fragment) {
    protected open val permissionCode: Int = 0
    protected open val permissionType = ""
    protected open val rationaleTitle = ""
    protected open val rationaleMessage = ""

    fun checkPermission() {
        // check if we have granted permission already
        if (fragment.context?.checkSelfPermission(permissionType) != PackageManager.PERMISSION_GRANTED) {
            //check if we should explain to user why we ask for permission(for the first time we haven't)
            if (fragment.shouldShowRequestPermissionRationale(permissionType)
            ) {
                // explanation the cause of request a permission
                AlertDialog.Builder(fragment.context)
                    .setTitle(rationaleTitle)
                    .setMessage(rationaleMessage)
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        //permission haven't been received
                        notifyFragment(fragment, false)
                    }
                    .show()
            } else {
                // if we shouldn't explain about permission asking
                requestPermission()
            }
        } else {
            // notify a fragment a permission was granted
            notifyFragment(fragment, true)
        }
    }

    private fun requestPermission() {
        //!!! IT CRUCIAL TO CALL IN ON ACTIVITY, NOT ON FRAGMENT!!!
        activity.requestPermissions(
            arrayOf(permissionType),
            permissionCode
        )
    }

    // when user react to a permission
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyFragment(fragment, true)
                    // show Toast of a permission granted
                    showPermissionToast(activity.applicationContext, permissions[0],true)
                } else {
                    notifyFragment(fragment, false)
                    // show Toast of a permission refused
                    showPermissionToast(activity.applicationContext, permissions[0],false)
                }
            }

        }
    }

    // here you notify Fragment about permission giving
    private fun notifyFragment(
        fragment: Fragment,
        permissionGranted: Boolean
    ) {
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        when (val activeFragment: Fragment? =
            fragment.childFragmentManager.primaryNavigationFragment) {
            is ProfileFragment -> {
                (activeFragment).onPermissionsResult(
                    permissionGranted
                )
            }
            is FeedsFragment -> {
                (activeFragment).onPermissionsResult(
                    permissionGranted
                )
            }
        }
    }

    //trim permission name by regex and return a permission message
    private fun showPermissionToast(context: Context, inputStr: String, permissionGranted: Boolean) {
        val toastMsg: String
        var s: String? = ""
        val p: Pattern = Pattern.compile("android.permission.(.*)")
        val m: Matcher = p.matcher(inputStr)
        if (m.find()) {
            s = m.group(1)
        }

        toastMsg = if (permissionGranted) {
            "Permission $s was granted"
        } else {
            "Permission $s wasn't granted"
        }

        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }
}