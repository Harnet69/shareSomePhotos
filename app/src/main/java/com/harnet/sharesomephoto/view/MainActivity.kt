package com.harnet.sharesomephoto.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.AppPermissions
import com.harnet.sharesomephoto.util.getFragment
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    // when get image from Image Chooser
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data?.data
        val bitmap: Bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage)

            // find current fragment
            val profileFragment = this.getFragment(ProfileFragment::class.java)
            val usersFragment = this.getFragment(UsersFragment::class.java)

            //TODO implement when instead if
            if(profileFragment != null){
                fragments.userImage_ImageView_Profile.setImageBitmap(bitmap)
                //TODO record this image to User account on Parse server
            }
            if(usersFragment != null){
                Toast.makeText(this, "Users fragment", Toast.LENGTH_LONG).show()
                //TODO implement method for an users fragment
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
