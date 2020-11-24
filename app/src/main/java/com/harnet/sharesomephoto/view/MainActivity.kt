package com.harnet.sharesomephoto.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.AppPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_fragment.*


class MainActivity : AppCompatActivity() {
    // permission service
    lateinit var appPermissions: AppPermissions

    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appPermissions = AppPermissions(this, fragments)
        //for back arrow
        navController = Navigation.findNavController(this, R.id.fragments)
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    //for back arrow
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    // make keyboard hides by clicking outside an EditView
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    // when user was asked for a permission
    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //here is the switcher of different kinds of permissions
        when(permissions[0]){
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                appPermissions.imagePermissionService.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
            }
        }
    }

    // when get image from Image Cooser
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data?.data
        val bitmap: Bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage)
            fragments.userImage_ImageView.setImageBitmap(bitmap)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
