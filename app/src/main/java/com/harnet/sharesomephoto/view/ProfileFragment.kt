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
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.model.ImageParsable
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.model.UserParsable
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.ProfileViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*
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

        // if user have been logged already
        if (currentUser == null) {
            login_block.visibility = View.VISIBLE
            profile_details_block.visibility = View.INVISIBLE
            isLogInMode = true

        } else {
            val thisUser =
                User(ParseUser.getCurrentUser().username, "", ParseUser.getCurrentUser().email)
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
                userEmail_LoginBlock.visibility = View.VISIBLE
                isLogInMode = false
            } else {
                login_signUp_btn_LoginBlock.text = getString(R.string.btn_logIn)
                logIn_signUp_TextView_LoginBlock.text = getString(R.string.label_orSignUp)
                userEmail_LoginBlock.visibility = View.GONE

                isLogInMode = true
            }

        }

        //login/signUp button
        login_signUp_btn_LoginBlock.setOnClickListener {
            logInSignUp()
        }

        // push on DONE btn of keyboard submit data
        userPassword_LoginBlock.editText?.let { submitUserData(it) }
        userEmail_LoginBlock.editText?.let { submitUserData(it) }

        // logout button
        logOut_btn_DetailsBlock.setOnClickListener {
            isLogInMode = true
            userName_LoginBlock.editText?.text?.clear()
            userPassword_LoginBlock.editText?.text?.clear()
            userEmail_LoginBlock.editText?.text?.clear()
            userEmail_LoginBlock.visibility = View.GONE
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
            if (isLogged) {
                // make bottom navigation bar visible
                activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)?.visibility = View.VISIBLE
                activity?.findViewById<CoordinatorLayout>(R.id.topAppBarBlock)?.visibility = View.VISIBLE

                if (ParseUser.getCurrentUser() != null) {
                    viewModel.mErrIsUserExists.value = null

                    // bind the user to view
                    val userForBinding =
                        User(
                            ParseUser.getCurrentUser().username,
                            "",
                            ParseUser.getCurrentUser().email
                        )
                    userForBinding.profileImgUrl =
                        ParseUser.getCurrentUser().get("profileImg").toString()

                    // get and bind user Profile image
                    dataBinding.user = userForBinding

                    login_block.visibility = View.INVISIBLE
                    profile_details_block.visibility = View.VISIBLE
                    isLogInMode = false
                }
            } else {
                // make bottom navigation bar invisible
                activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)?.visibility = View.INVISIBLE
                activity?.findViewById<CoordinatorLayout>(R.id.topAppBarBlock)?.visibility = View.INVISIBLE
                login_block.visibility = View.VISIBLE
                profile_details_block.visibility = View.INVISIBLE
                isLogInMode = true
            }
        })

        viewModel.mErrUserName.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userName_LoginBlock.error = errorMessage
            } else {
                userName_LoginBlock.error = null
            }
        })

        viewModel.mErrUserPassword.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userPassword_LoginBlock.error = errorMessage
            } else {
                userPassword_LoginBlock.error = null
            }
        })

        viewModel.mErrIsUserEmailValid.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userEmail_LoginBlock.error = errorMessage
            } else {
                userEmail_LoginBlock.error = null
            }
        })

        viewModel.mErrIsUserExists.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userName_LoginBlock.error = errorMessage
            } else {
                userName_LoginBlock.error = null
            }
        })

        viewModel.mErrUserLoginOrPass.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userName_LoginBlock.error = errorMessage
                userPassword_LoginBlock.error = " "
            } else {
                userName_LoginBlock.error = null
                userPassword_LoginBlock.error = null
            }
        })

        viewModel.mErrUserNameLength.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                userName_LoginBlock.error = errorMessage
            } else {
                userEmail_LoginBlock.error = null
            }
        })
    }

    // handle logIn signUp functionality
    private fun logInSignUp() {
        if (isLogInMode) {
            viewModel.logIn(
                userName_LoginBlock.editText?.text.toString(),
                userPassword_LoginBlock.editText?.text.toString()
            )
        } else {
            viewModel.signUp(
                User(
                    userName_LoginBlock.editText?.text.toString(),
                    userPassword_LoginBlock.editText?.text.toString(),
                    userEmail_LoginBlock.editText?.text.toString()
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

    override fun onStop() {
        super.onStop()
        viewModel.clearErrors()
    }
}