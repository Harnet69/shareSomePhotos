package com.harnet.sharesomephoto.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.AppPermissions
import com.harnet.sharesomephoto.util.convertImageDataToBitmap
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    // permission service
    lateinit var appPermissions: AppPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup bottom bar
        setUpNavigation()

        appPermissions = AppPermissions(this, fragments)
    }

    private fun setUpNavigation() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragments) as NavHostFragment?
        navHostFragment?.let {
            NavigationUI.setupWithNavController(
                bottomNavigationView,
                it.navController
            )
        }
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
        //switcher of different kinds of permissions
        if (permissions.isNotEmpty()) {
            when (permissions[0]) {
                android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    appPermissions.imagesService.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                }
            }
        }
    }

    // when get image from image library by Image Chooser
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //switcher of different kinds of requests codes
        when (requestCode) {
            // image gallery access
            resources.getString(R.string.permissionImagesCode).toInt() -> {
                // convert a data to bitmap
                val imageBtm = convertImageDataToBitmap(this, data)

                when (val activeFragment: Fragment? =
                    fragments.childFragmentManager.primaryNavigationFragment) {
                    is ProfileFragment -> {
                        imageBtm?.let { image ->
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToImagePreviewFragment(
                                    image,
                                    "profile"
                                )
                            activeFragment.view?.let {
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }
                    is FeedsFragment -> {
                        imageBtm?.let { image ->
                            val action =
                                FeedsFragmentDirections.actionFeedsFragmentToImagePreviewFragment(
                                    image,
                                    "feeds"
                                )
                            activeFragment.view?.let {
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }
                }
            }
        }
    }
}
