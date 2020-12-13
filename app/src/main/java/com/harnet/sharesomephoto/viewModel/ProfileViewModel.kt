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

        // are fields valid
        if (checkUserInputForEmpty(newUser) && checkUserNameAndPassForWhiteSpaces(newUser)) {
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
                if (checkUserNameAndPassForWhiteSpaces(user)) {
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

    // check if user fields empty
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
                    }
                } else {
                    mIsUserEmailValid.value =
                        getApplication<Application>().getString(R.string.field_cant_be_empty)
                }
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.field_cant_be_empty)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.field_cant_be_empty)
        }
        return false
    }

    //check if user fields has a whitespace
    private fun checkUserNameAndPassForWhiteSpaces(newUser: User): Boolean {
        if (!newUser.name.contains(" ")) {
            if (!newUser.password.contains(" ")) {
                return true
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.whiteSpaces_not_allowed)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.whiteSpaces_not_allowed)
        }
        return false
    }

    // check if email valid
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    // clear input fields errors
    private fun clearErrors(){
        mErrUserName.value = null
        mErrUserPassword.value = null
        mIsUserEmailValid.value = null
    }
}