package com.harnet.sharesomephoto.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

    lateinit var userNameField:EditText
    lateinit var userPswField: EditText
    lateinit var userEmailField: EditText
    lateinit var signUpBtn: Button

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

        userNameField = view.findViewById<EditText>(R.id.userName_editText)
        userPswField = view.findViewById<EditText>(R.id.userPassword_editText)
        userEmailField = view.findViewById<EditText>(R.id.userEmail_editText)
        signUpBtn = view.findViewById<Button>(R.id.sign_up_btn)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        signUpBtn.setOnClickListener {
            viewModel.addNewUser(
                User(
                    userNameField.text.toString(),
                    userPswField.text.toString(),
                    userEmailField.text.toString()
                )
            )
        }

        observeModel()
    }

    private fun observeModel() {
        viewModel.mIsUserExists.observe(viewLifecycleOwner, Observer { isExists ->
            if (isExists) {
                userNameField.setBackgroundColor(Color.rgb( 201, 54, 49 ))
            } else {
                //TODO log successfully
            }
        })
    }
}