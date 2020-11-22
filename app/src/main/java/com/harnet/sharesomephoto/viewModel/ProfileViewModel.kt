package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.lookatthis.model.User
import com.parse.LogInCallback
import com.parse.ParseUser


class ProfileViewModel(application: Application) : BaseViewModel(application) {
    val mIsUserExists = MutableLiveData<Boolean>()
    val mIsUserLogged = MutableLiveData<Boolean>()

    // sign Up a new user
    fun signUp(newUser: User) {
        mIsUserExists.setValue(false)

        // check if all fields not empty
        if (checkUserInputForEmpty(newUser) && checkUserInputForWhiteSpaces(newUser)) {
            val parseUser = ParseUser()
            //create a new user
            parseUser.username = newUser.name
            parseUser.setPassword(newUser.password.trim())
            parseUser.email = newUser.email


            // sign in user automatically, till login functionality will be implemented
            parseUser.signUpInBackground { e ->
                if (e == null) {
                    Toast.makeText(getApplication(), "Signed Up Successfully", Toast.LENGTH_SHORT)
                        .show()
                    isLogged()
                } else {
                    e.printStackTrace()
                    Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
                    mIsUserExists.setValue(true)
                }
            }
        }
    }

    //log in
    fun logIn(userName: String, userPassword: String) {
        if (checkUserInputForWhiteSpaces(User(userName, userPassword, ""))) {
            ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
                if (user != null) {
                    mIsUserLogged.setValue(true)
                } else {
                    Toast.makeText(
                        getApplication(),
                        e.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    e.printStackTrace()
                }
            })
        }
    }

    //log out
    fun logOut() {
        ParseUser.logOut()
        Toast.makeText(getApplication(), "Log out", Toast.LENGTH_SHORT)
            .show()
        mIsUserLogged.setValue(false)
    }

    //check if the user logged in
    fun isLogged(): ParseUser? {
        if (ParseUser.getCurrentUser() != null) {
            mIsUserLogged.setValue(true)
        } else {
            mIsUserLogged.setValue(false)
        }
        return ParseUser.getCurrentUser()
    }

    private fun checkUserInputForEmpty(newUser: User): Boolean {
        // check if fields not empty
        if (!newUser.name.equals("")) {
            if (!newUser.password.equals("")) {
                if (!newUser.email.equals("")) {
                    if (isValidEmail(newUser.email)) {
                        return true
                    } else {
                        Toast.makeText(getApplication(), "Wrong e-mail format", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(getApplication(), "E-mail is required", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(getApplication(), "Password can't be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(getApplication(), "Name can't be empty", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkUserInputForWhiteSpaces(newUser: User): Boolean {
        if (!newUser.name.contains(" ")) {
            if (!newUser.password.contains(" ")) {
                return true
            } else {
                Toast.makeText(
                    getApplication(),
                    "Whitespaces not allowed in password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(getApplication(), "Whitespaces not allowed in name", Toast.LENGTH_SHORT)
                .show()
        }


        return false
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun submitUserData(textView: TextView, aimBtn: Button){
        textView.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                Log.i("tweet", "Enter pressed")
            }
            false
        })
    }
}