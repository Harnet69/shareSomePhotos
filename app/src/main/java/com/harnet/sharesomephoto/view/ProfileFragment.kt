package com.harnet.sharesomephoto.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.lookatthis.model.User
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.viewModel.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var dataBinding: ProfileFragmentBinding

    lateinit var userLoginBlock: LinearLayout
    lateinit var userNameField: EditText
    lateinit var userPswField: EditText
    lateinit var userEmailField: EditText
    lateinit var signUpBtn: Button
    lateinit var logInBtn: Button

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
        userNameField = view.findViewById(R.id.userName_editText)
        userPswField = view.findViewById(R.id.userPassword_editText)
        userEmailField = view.findViewById(R.id.userEmail_editText)
        signUpBtn = view.findViewById(R.id.sign_up_btn)
        logInBtn = view.findViewById(R.id.login_btn)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // if user have been logged already
        val isLogged = viewModel.isLogged()
        if (isLogged == null) {
            signUpBtn.setOnClickListener {
                viewModel.addNewUser(
                    User(
                        userNameField.text.toString(),
                        userPswField.text.toString(),
                        userEmailField.text.toString()
                    )
                )
            }
            //TODO implement Login functionality for button here
            logInBtn.setOnClickListener {
                viewModel.logIn(userNameField.text.toString(), userPswField.text.toString())
            }
            observeModel()
        } else {
            userLoginBlock.setVisibility(View.GONE)
            Toast.makeText(context, "Hello ${isLogged.username}", Toast.LENGTH_LONG).show()
            //TODO profile details block

        }
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
            if (isLogged) {
                userLoginBlock.setVisibility(View.GONE)
                //TODO switch on the profile details block
                Toast.makeText(context, "Hello", Toast.LENGTH_LONG).show()
            }
        })
    }
}