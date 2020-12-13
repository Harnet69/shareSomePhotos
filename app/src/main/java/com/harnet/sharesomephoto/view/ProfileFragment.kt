package com.harnet.sharesomephoto.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.profile_details_block.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_login_block.*

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
            thisUser.profileImgUrl = ParseUser.getCurrentUser().get("profileImg").toString()

            dataBinding.user = thisUser
            login_block.visibility = View.INVISIBLE
            profile_details_block.visibility = View.VISIBLE

        }

        // LogIn signUp switcher
        logIn_signUp_TextView_LoginBlock.setOnClickListener {
            //functionality of switching and Text btns
            if (isLogInMode) {
                login_signUp_btn_LoginBlock.text = getString(R.string.btn_signUp)
                logIn_signUp_TextView_LoginBlock.text = getString(R.string.label_orLogIn)
                userEmailBlock_LoginBlock.visibility = View.VISIBLE
                isLogInMode = false
            } else {
                login_signUp_btn_LoginBlock.text = getString(R.string.btn_logIn)
                logIn_signUp_TextView_LoginBlock.text = getString(R.string.label_orSignUp)
                userEmailBlock_LoginBlock.visibility = View.GONE
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
            userName_LoginBlock.text?.clear()
            userPassword_LoginBlock.text?.clear()
            userEmail_LoginBlock.text?.clear()
            userEmailBlock_LoginBlock.visibility = View.GONE
            img_ItemUser.setImageResource(R.drawable.ic_user_photo)
            viewModel.logOut()

        }

        // Add/change user image
        img_ItemUser.setOnLongClickListener {
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
        viewModel.mIsUserLogged.observe(viewLifecycleOwner, Observer { isLogged ->
            //switching between profile details and login blocks
            if (isLogged && ParseUser.getCurrentUser() != null) {
                // bind the user to view
                    val userForBinding = User(ParseUser.getCurrentUser().username, "", ParseUser.getCurrentUser().email)
                    userForBinding.profileImgUrl =
                        ParseUser.getCurrentUser().get("profileImg").toString()

                    // get and bind user Profile image
                    dataBinding.user = userForBinding

                login_block.visibility = View.INVISIBLE
                profile_details_block.visibility = View.VISIBLE

//                Toast.makeText(context, "Hello " + isLoggedGetUser()?.username, Toast.LENGTH_LONG)
//                    .show()
                isLogInMode = false
            } else {
                login_block.visibility = View.VISIBLE
                profile_details_block.visibility = View.INVISIBLE
                isLogInMode = true
            }
        })

        viewModel.errUserName.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                //TODO Implement response to view
                userNameBlock_LoginBlock.error = it
            }
        })

        viewModel.errUserPassword.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                userPasswordBlock_LoginBlock.error = it
            }
        })

        viewModel.isUserEmailValid.observe(viewLifecycleOwner, Observer { isEmailValid ->
            isEmailValid?.let {
                if(!it){
                    userEmailBlock_LoginBlock.error = "Wrong email format"
                }
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