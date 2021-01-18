package com.harnet.sharesomephoto.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.AppPermissions
import com.harnet.sharesomephoto.util.convertImageDataToBitmap
import com.harnet.sharesomephoto.util.findNewMessage
import com.harnet.sharesomephoto.viewModel.MainViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private lateinit var activity: Activity

    private lateinit var bottomNavigationView: BottomNavigationView

    // permission service
    lateinit var appPermissions: AppPermissions

    // Repeating
    private var mInterval: Int = 5000 // 5 seconds by default, can be changed later

    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // setup bottom bar
        setUpNavigation()

        activity = this

        appPermissions = AppPermissions(this, fragments)

        observeViwModel()

        //repeat
        mHandler = Handler()
        startRepeatingTask()

    }

    private fun observeViwModel(){
        viewModel.mIsNewMsgTrigger.observeForever {
            viewModel.markChatsBtnAsHasNewMsg(this, it)
        }

        viewModel.mNewMessages.observeForever { newMsgs ->
            Log.i("newMessages", "New messages: ${newMsgs[newMsgs.size-1]}")
        }
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
                    appPermissions.imagesPermissionService.onRequestPermissionsResult(
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

    //repeating
    private var mStatusChecker: Runnable? = object : Runnable {
        override fun run() {
            try {
                //is new messages
                    ParseUser.getCurrentUser()?.let {
                        viewModel.refresh()
//                        Log.i("HasNewMessage", "refresh ")
                    }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler?.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun startRepeatingTask() {
        mStatusChecker!!.run()
    }

    private fun stopRepeatingTask() {
        mHandler?.removeCallbacks(mStatusChecker!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }
}
