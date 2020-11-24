package com.harnet.sharesomephoto.view

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.model.UserParsable
import com.harnet.sharesomephoto.service.Imageable
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser

class ProfileFragment : Fragment(), UserParsable, Imageable {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var dataBinding: ProfileFragmentBinding

    lateinit var userLoginBlock: LinearLayout
    lateinit var userProfileDetailsBlock: ConstraintLayout

    lateinit var userNameField: EditText
    lateinit var userPswField: EditText
    lateinit var userEmailField: EditText
    lateinit var logInSignUpTextView: TextView
    lateinit var logInSignUpBtn: Button
    lateinit var logOut: Button

    lateinit var userNameTextView: TextView
    lateinit var userEmailTextView: TextView

    var isLogInMode: Boolean = true

    var currentUser: ParseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // DataBinding
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userLoginBlock = view.findViewById(R.id.login_block)
        userProfileDetailsBlock = view.findViewById(R.id.profile_details_block)
        userNameField = view.findViewById(R.id.userName_editText)
        userPswField = view.findViewById(R.id.userPassword_editText)
        userEmailField = view.findViewById(R.id.userEmail_editText)
        logInSignUpTextView = view.findViewById(R.id.logIn_signUp_TextView)
        logInSignUpBtn = view.findViewById(R.id.login_signUp_btn)
        logOut = view.findViewById(R.id.logOut_btn)

        userNameTextView = view.findViewById(R.id.userName_TextView)
        userEmailTextView = view.findViewById(R.id.userEmail_TextView)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        currentUser = viewModel.isLogged()

        //set focus to user name edit field
        userNameField.requestFocus()

        // if user have been logged already !!! Should be in a separate block
        if (ParseUser.getCurrentUser() == null) {
            userLoginBlock.setVisibility(View.VISIBLE)
            userProfileDetailsBlock.setVisibility(View.INVISIBLE)

        } else {
            userLoginBlock.setVisibility(View.INVISIBLE)
            userProfileDetailsBlock.setVisibility(View.VISIBLE)
            userNameTextView.text = "Name: " + currentUser?.username
            userEmailTextView.text = "E-mail: " + currentUser?.email
        }

        // LogIn signUp switcher
        logInSignUpTextView.setOnClickListener {
            //TODO here is all functionality of switching and Text btns
            if (isLogInMode) {
                logInSignUpBtn.text = "Sign Up"
                logInSignUpTextView.text = "or, Log in"
                userEmailField.visibility = View.VISIBLE
                isLogInMode = false
            } else {
                logInSignUpBtn.text = "Log in"
                logInSignUpTextView.text = "or, Sign Up"
                userEmailField.visibility = View.GONE
                isLogInMode = true
            }

        }

        //login/signUp button
        logInSignUpBtn.setOnClickListener {
            logInSignUp()
        }

        // push on DONE btn of keyboard submit data
        submitUserData(userPswField)
        submitUserData(userEmailField)

        // logout button
        logOut.setOnClickListener {
            viewModel.logOut()
        }

        // Add/change user image
        dataBinding.userImageImageView.setOnClickListener {
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

                userLoginBlock.setVisibility(View.INVISIBLE)
                userProfileDetailsBlock.setVisibility(View.VISIBLE)
                Toast.makeText(context, "Hello " + isLoggedGetUser()?.username, Toast.LENGTH_LONG).show()
                isLogInMode = false
            } else {
                userLoginBlock.setVisibility(View.VISIBLE)
                userProfileDetailsBlock.setVisibility(View.INVISIBLE)
                isLogInMode = true
            }
        })
    }

    // handle logIn signUp functionality
    private fun logInSignUp() {
        if (isLogInMode) {
            viewModel.logIn(userNameField.text.toString(), userPswField.text.toString())
        } else {
            viewModel.signUp(
                User(
                    userNameField.text.toString(),
                    userPswField.text.toString(),
                    userEmailField.text.toString()
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
//            chooseImage()
//        view?.let {
//            Navigation.findNavController(it).navigate(ProfileFragmentDirections.actionProfileFragmentToImageFragment())
//        }
            //TODO implement image choosing functionality Imageable interface
        }
    }


}