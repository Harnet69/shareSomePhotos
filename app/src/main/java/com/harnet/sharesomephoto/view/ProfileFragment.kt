package com.harnet.sharesomephoto.view

import android.app.Activity
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
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.model.UserParsable
import com.harnet.sharesomephoto.service.Imageable
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.login_block.*
import kotlinx.android.synthetic.main.profile_details_block.*
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment(), UserParsable, Imageable {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var dataBinding: ProfileFragmentBinding

    var isLogInMode: Boolean = true
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

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        currentUser = viewModel.getCurrentUserIfLogged()

        //set focus to user name edit field
        userName_LoginBlock.requestFocus()

        // if user have been logged already !!! Should be in a separate block
        if (ParseUser.getCurrentUser() == null) {
            login_block.setVisibility(View.VISIBLE)
            profile_details_block.setVisibility(View.INVISIBLE)

        } else {
            login_block.setVisibility(View.INVISIBLE)
            profile_details_block.setVisibility(View.VISIBLE)
            userName_DetailsBlock.text = "Name: " + currentUser?.username
            userEmail_DetailsBlock.text = "E-mail: " + currentUser?.email
        }

        // LogIn signUp switcher
        logIn_signUp_TextView_LoginBlock.setOnClickListener {
            //TODO here is all functionality of switching and Text btns
            if (isLogInMode) {
                login_signUp_btn_LoginBlock.text = "Sign Up"
                logIn_signUp_TextView_LoginBlock.text = "or, Log in"
                userEmail_LoginBlock.visibility = View.VISIBLE
                isLogInMode = false
            } else {
                login_signUp_btn_LoginBlock.text = "Log in"
                logIn_signUp_TextView_LoginBlock.text = "or, Sign Up"
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
            userName_LoginBlock.text.clear()
            userPassword_LoginBlock.text.clear()
            userEmail_LoginBlock.text.clear()
            userImage_ImageView_Profile.setImageResource(R.drawable.ic_user_photo)
            viewModel.logOut()

        }

        // Add/change user image
        userImage_ImageView_Profile.setOnClickListener {
            if(! isLogInMode){
                (activity as MainActivity).appPermissions.imagePermissionService.checkPermission()
            }else{
                Toast.makeText(context, "Log in at first", Toast.LENGTH_SHORT).show()
            }
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
                dataBinding.user = isLoggedGetUser()?.let { isLoggedGetUser()?.let { it1 -> User(it.username, "", it1.email) } }

                login_block.setVisibility(View.INVISIBLE)
                profile_details_block.setVisibility(View.VISIBLE)
                Toast.makeText(context, "Hello " + isLoggedGetUser()?.username, Toast.LENGTH_LONG).show()
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
            viewModel.logIn(userName_LoginBlock.text.toString(), userPassword_LoginBlock.text.toString())
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
        if(permissionGranted){
            openImageChooser(activity as Activity)
        }
    }


}