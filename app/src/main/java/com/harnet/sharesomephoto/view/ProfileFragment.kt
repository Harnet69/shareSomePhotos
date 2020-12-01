package com.harnet.sharesomephoto.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.model.ImageParsable
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.model.UserParsable
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.login_block.*
import kotlinx.android.synthetic.main.profile_details_block.*
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment(), UserParsable, ImageParsable {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var dataBinding: ProfileFragmentBinding

    var isLogInMode: Boolean = false
    var currentUser: ParseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setActivityTitle("Your profile")
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        currentUser = viewModel.getCurrentUserIfLogged()

        //set focus to user name edit field
        userName_LoginBlock.requestFocus()

        // if user have been logged already !!! Should be in a separate block
        if (currentUser == null) {
            login_block.visibility = View.VISIBLE
            profile_details_block.visibility = View.INVISIBLE
            isLogInMode = true

        } else {
            val thisUser = User(ParseUser.getCurrentUser().username, "", ParseUser.getCurrentUser().email)
            thisUser.profileImgId = ParseUser.getCurrentUser().get("profileImg").toString()

            dataBinding.user = thisUser
//            context?.let { getProgressDrawable(it) }?.let {
//                userImage_ImageView_Profile.loadImage(ParseUser.getCurrentUser().get("profileImg").toString(), it)
//            }
            login_block.visibility = View.INVISIBLE
            profile_details_block.visibility = View.VISIBLE

        }

        // LogIn signUp switcher
        logIn_signUp_TextView_LoginBlock.setOnClickListener {
            //functionality of switching and Text btns
            if (isLogInMode) {
                login_signUp_btn_LoginBlock.text = getString(R.string.signUp)
                logIn_signUp_TextView_LoginBlock.text = getString(R.string.orLogIn)
                userEmail_LoginBlock.visibility = View.VISIBLE
                isLogInMode = false
            } else {
                login_signUp_btn_LoginBlock.text = getString(R.string.logIn)
                logIn_signUp_TextView_LoginBlock.text = getString(R.string.orSignUp)
                userEmail_LoginBlock.visibility = View.GONE
                isLogInMode = true
            }

        }

        //login/signUp button
        login_signUp_btn_LoginBlock.setOnClickListener {
            logInSignUp()
        }

        // push on DONE btn of keyboard submit data
        submitUserData(userPassword_LoginBlock)
        submitUserData(userEmail_LoginBlock)

        // logout button
        logOut_btn_DetailsBlock.setOnClickListener {
            isLogInMode = true
            userName_LoginBlock.text.clear()
            userPassword_LoginBlock.text.clear()
            userEmail_LoginBlock.text.clear()
            userImage_ImageView_Profile.setImageResource(R.drawable.ic_user_photo)
            viewModel.logOut()

        }

        // Add/change user image
        userImage_ImageView_Profile.setOnLongClickListener {
            if (!isLogInMode) {
                if (this.context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openImageChooser(activity as Activity)
                } else {
                    (activity as MainActivity).appPermissions.imagesService.checkPermission()
                }
            } else {
                Toast.makeText(context, "Log in at first", Toast.LENGTH_SHORT).show()
            }
            return@setOnLongClickListener true
        }
        observeModel()
    }

    private fun observeModel() {
        viewModel.mIsUserExists.observe(viewLifecycleOwner, Observer { isExists ->
            // if user exists
        })

        viewModel.mIsUserLogged.observe(viewLifecycleOwner, Observer { isLogged ->
            //switching between profile details and login blocks
            if (isLogged && ParseUser.getCurrentUser() != null) {
                // bind the user to view
                currentUser?.let {
                    val userForBinding = User(it.username, "", it.email)
                    userForBinding.profileImgId = ParseUser.getCurrentUser().get("profileImg").toString()

                    // get and bind user Profile image
                    dataBinding.user = userForBinding
                }

                login_block.visibility = View.INVISIBLE
                profile_details_block.visibility = View.VISIBLE

                Toast.makeText(context, "Hello " + isLoggedGetUser()?.username, Toast.LENGTH_LONG)
                    .show()
                isLogInMode = false
            } else {
                login_block.setVisibility(View.VISIBLE)
                profile_details_block.setVisibility(View.INVISIBLE)
                isLogInMode = true
            }
        })
    }

    // handle logIn signUp functionality
    private fun logInSignUp() {
        if (isLogInMode) {
            viewModel.logIn(
                userName_LoginBlock.text.toString(),
                userPassword_LoginBlock.text.toString()
            )
        } else {
            viewModel.signUp(
                User(
                    userName_LoginBlock.text.toString(),
                    userPassword_LoginBlock.text.toString(),
                    userEmail_LoginBlock.text.toString()
                )
            )
        }
    }

    // when user push DONE(Enter) after filling logIn or SignUp form
    private fun submitUserData(textView: TextView) {
        textView.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                logInSignUp()
            }
            false
        })
    }

    // method is called when activity get a result of user  permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            openImageChooser(activity as Activity)
        }
    }
}