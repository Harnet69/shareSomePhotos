package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
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
        if (checkUserInput(newUser)) {
            val parseUser = ParseUser()
            //create a new user
            parseUser.username = newUser.name
            parseUser.setPassword(newUser.password)
            parseUser.email = newUser.email


            // sign in user automatically, till login functionality will be implemented
            parseUser.signUpInBackground { e ->
                if (e == null) {
                    Toast.makeText(getApplication(), "Signed Up Successfully", Toast.LENGTH_SHORT)
                        .show()
                    mIsUserExists.setValue(false)
                    isLogged()
                } else {
                    Log.i("tweet", "addUser: smth wrong with sign in" + e.printStackTrace())
                    Toast.makeText(getApplication(), "User exists", Toast.LENGTH_SHORT).show()
                    mIsUserExists.setValue(true)
                }
            }
        }
    }


    //log in
    fun logIn(userName: String, userPassword: String) {
        ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
            if (user != null) {
                mIsUserLogged.setValue(true)
            } else {
                Toast.makeText(getApplication(), "Invalid username/password", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        })
    }

    //log out
    fun logOut(){
        ParseUser.logOut()
//        Toast.makeText(getApplication(), "Log out", Toast.LENGTH_SHORT)
//            .show()
        mIsUserLogged.setValue(false)
    }

    //check if the user logged in
    fun isLogged(): ParseUser? {
        if (ParseUser.getCurrentUser() != null) {
            mIsUserLogged.setValue(true)
            Log.i("tweet", "isLogged: you logged as: ${ParseUser.getCurrentUser().username}")
        } else {
            mIsUserLogged.setValue(false)
            Log.i("tweet", "isLogged: ${ParseUser.getCurrentUser()}")
        }
        return ParseUser.getCurrentUser()
    }

    private fun checkUserInput(newUser: User): Boolean {
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

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}