package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.User
import com.parse.LogInCallback
import com.parse.ParseUser

class ProfileViewModel(application: Application) : BaseViewModel(application) {
    val mIsUserLogged = MutableLiveData<Boolean>()

    val mErrUserName = MutableLiveData<String>()
    val mErrUserPassword = MutableLiveData<String>()
    val mIsUserEmailValid = MutableLiveData<String>()

    // sign Up a new user
    fun signUp(newUser: User) {
        clearErrors()
        // check if all fields not empty
        if (checkUserInputForEmpty(newUser) && checkUserInputForWhiteSpaces(newUser)) {
            //create a new user
            val parseUser = ParseUser()
            parseUser.username = newUser.name.trim()
            parseUser.setPassword(newUser.password.trim())
            parseUser.email = newUser.email.trim()

            // sign in user
            parseUser.signUpInBackground { e ->
                if (e == null) {
                    Toast.makeText(getApplication(), "Signed Up Successfully", Toast.LENGTH_SHORT)
                        .show()
                    getCurrentUserIfLogged()
                } else {
                    Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    //log in
    fun logIn(userName: String, userPassword: String) {
        clearErrors()
        if (userName != "") {
            if (userPassword != "") {
                val user = User(userName, userPassword, "")
                if (checkUserInputForWhiteSpaces(user)) {
                    ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
                        if (e == null && user != null) {
                            mIsUserLogged.setValue(true)
                        } else {
                            Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    })
                }
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.field_cant_be_empty)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.field_cant_be_empty)
        }
    }

    //log out
    fun logOut() {
        clearErrors()

        ParseUser.logOut()
        Toast.makeText(getApplication(), "Log out", Toast.LENGTH_SHORT).show()
        mIsUserLogged.setValue(false)
    }

    //check if the user logged in
    fun getCurrentUserIfLogged(): ParseUser? {
        if (ParseUser.getCurrentUser() != null) {
            mIsUserLogged.setValue(true)
        } else {
            mIsUserLogged.setValue(false)
        }
        return ParseUser.getCurrentUser()
    }

    private fun checkUserInputForEmpty(newUser: User): Boolean {
        // check if fields not empty
        if (newUser.name != "") {
            if (newUser.password != "") {
                if (newUser.email != "") {
                    if (isValidEmail(newUser.email)) {
                        return true
                    } else {
                        mIsUserEmailValid.value =
                            getApplication<Application>().getString(R.string.wrong_email_format)
                        Toast.makeText(
                            getApplication(),
                            "Wrong e-mail format",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    mIsUserEmailValid.value =
                        getApplication<Application>().getString(R.string.field_cant_be_empty)
                    Toast.makeText(getApplication(), "E-mail is required", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.field_cant_be_empty)
                Toast.makeText(getApplication(), "Password can't be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.field_cant_be_empty)
            Toast.makeText(getApplication(), "Name can't be empty", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkUserInputForWhiteSpaces(newUser: User): Boolean {
        if (!newUser.name.contains(" ")) {
            if (!newUser.password.contains(" ")) {
                return true
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.whiteSpaces_not_allowed)
                Toast.makeText(
                    getApplication(),
                    "Whitespaces not allowed in password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.whiteSpaces_not_allowed)
            Toast.makeText(
                getApplication(),
                "Whitespaces not allowed in name",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        return false
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    // clear all errors
    private fun clearErrors(){
        mErrUserName.value = null
        mErrUserPassword.value = null
        mIsUserEmailValid.value = null
    }
}