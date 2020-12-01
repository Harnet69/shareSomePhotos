package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
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
                    mIsUserExists.setValue(true)
                }
            }
        }
    }

    //log in
    fun logIn(userName: String, userPassword: String) {
        if (checkUserInputForWhiteSpaces(User(userName, userPassword, ""))) {
            ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
                if (e == null && user != null) {
                    mIsUserLogged.setValue(true)
                } else {
                    Toast.makeText(getApplication(), e.message,Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            })
        }
    }

    //log out
    fun logOut() {
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

    // get Profile image and set it to Profile's image
//    //TODO move it to dataBinding
//    fun setProfileImage(profileImageView: ImageView) {
//        val userProfileImageId = ParseUser.getCurrentUser().get("profileImg")
//        val query = ParseQuery<ParseObject>("Image")
//        query.whereEqualTo("objectId", userProfileImageId)
//
//        query.findInBackground(FindCallback { objects, parseObjectError ->
//            if (parseObjectError == null) {
//                if (objects.isNotEmpty()) {
//                    for (image in objects) {
//                        val parseFile = image.getParseFile("image")
//                        profileImageView.loadImage(
//                            parseFile.url,
//                            getProgressDrawable(profileImageView.context)
//                        )
//                    }
//                } else {
//                    Toast.makeText(profileImageView.context, "No users with images", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                parseObjectError.printStackTrace()
//                Toast.makeText(profileImageView.context, parseObjectError.message, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}