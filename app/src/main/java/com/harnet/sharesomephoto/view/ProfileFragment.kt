package com.harnet.sharesomephoto.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.lookatthis.model.User
import com.harnet.lookatthis.model.UserParsable
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser

class ProfileFragment : Fragment(), UserParsable {
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
    ): View? {
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
            if(isLogInMode){
                logInSignUpBtn.text = "Sign Up"
                logInSignUpTextView.text = "or, Log in"
                isLogInMode = false
            }else{
                logInSignUpBtn.text = "Log in"
                logInSignUpTextView.text = "or, Sign Up"
                isLogInMode = true
            }

        }
        //login button
        logInSignUpBtn.setOnClickListener {
            if(isLogInMode){
                viewModel.logIn(userNameField.text.toString(), userPswField.text.toString())
            }else{
                viewModel.signUp(
                    User(
                        userNameField.text.toString(),
                        userPswField.text.toString(),
                        userEmailField.text.toString()
                    )
                )
            }
        }
        // logout button
        logOut.setOnClickListener {
            viewModel.logOut()
        }

        observeModel()
    }

    private fun observeModel() {
        viewModel.mIsUserExists.observe(viewLifecycleOwner, Observer { isExists ->
            if (isExists) {
                userNameField.setBackgroundColor(Color.rgb(201, 54, 49))
            } else {
                //TODO log successfully
            }
        })

        viewModel.mIsUserLogged.observe(viewLifecycleOwner, Observer { isLogged ->
            if (isLogged && ParseUser.getCurrentUser() != null) {
                userLoginBlock.setVisibility(View.INVISIBLE)
                userProfileDetailsBlock.setVisibility(View.VISIBLE)
                //switching between profile details and login blocks
                Toast.makeText(context, "Hello " + isLogged()?.username, Toast.LENGTH_LONG).show()
                userNameTextView.text = "Name: " + isLogged()?.username
                userEmailTextView.text = "E-mail: " + isLogged()?.email
            } else {
                userLoginBlock.setVisibility(View.VISIBLE)
                userProfileDetailsBlock.setVisibility(View.INVISIBLE)
            }
        })
    }
}