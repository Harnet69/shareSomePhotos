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

    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup bottom bar
        setUpNavigation()

        appPermissions = AppPermissions(this, fragments)

//        //for back arrow
//        navController = Navigation.findNavController(this, R.id.fragments)
//        NavigationUI.setupActionBarWithNavController(this, navController)
//
//    }
//
//    //for back arrow
//    override fun onSupportNavigateUp(): Boolean {
//        return NavigationUI.navigateUp(navController, null)


//        //set image to profile menu item
//        val profileMenuItem =
//            topAppBar.findViewById(R.id.profileFragment) as ActionMenuItemView
//        ParseUser.getCurrentUser()?.let { parseUser ->
//            loadImageToMenuItem(
//                this,
//                profileMenuItem,
//                parseUser.get("profileImg").toString()
//            )
//        }
//
//        // top appbar menu
//        topAppBar.setNavigationOnClickListener {
//            Toast.makeText(this, "Press to navigation", Toast.LENGTH_LONG).show()
//        }
//
//        topAppBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.usersFragment -> {
////                    Toast.makeText(this, "Go to users list", Toast.LENGTH_LONG).show()
//                    goToUsers()
//                    true
//                }
//                R.id.profileFragment -> {
////                    Toast.makeText(this, "Go to profile", Toast.LENGTH_LONG).show()
//                    goToProfile()
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    // redirect to the Profile page
//    private fun goToProfile() {
//        fragments.view?.let {
//            Navigation.findNavController(it)
//                .navigate(FeedsFragmentDirections.actionFeedsFragmentToProfileFragment())
//        }
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

    // redirect to the Users list
    private fun goToUsers() {
        fragments.view?.let {
            Navigation.findNavController(it)
                .navigate(FeedsFragmentDirections.actionFeedsFragmentToUsersFragment())
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
