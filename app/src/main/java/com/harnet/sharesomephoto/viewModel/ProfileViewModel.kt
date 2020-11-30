package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
import com.parse.*

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

    //set user's profile image
    fun setProfileImg(imageView: ImageView){
        val parseUser = ParseUser.getCurrentUser()
        val imageId = parseUser.get("profileImg").toString()

        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("username", parseUser)
        query.whereEqualTo("objectId", imageId)

        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        imageView.loadImage(
                            parseFile.url,
                            getProgressDrawable(imageView.context)
                        )
                    }
                } else {
                    Log.i("userImages", "No image of the user")
                }
            } else {
                Toast.makeText(imageView.context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        })
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


    // addProfileImage
    fun addProfileImageUrl(imgUrl: String){
        val parserUser = ParseUser.getCurrentUser()
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
            if(e == null){
                parserUser.put("profileImg", imgUrl)
                parserUser.saveInBackground()
                Toast.makeText(getApplication(), "Image was changed $imgUrl", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

//    fun setProfileImgUrl(){
//        val parserUser = ParseUser.getCurrentUser()
//        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
//        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
//            if(e == null){
//                Toast.makeText(getApplication(), `object`.get("profileImg").toString(), Toast.LENGTH_SHORT).show()
//
//            }else{
//                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}