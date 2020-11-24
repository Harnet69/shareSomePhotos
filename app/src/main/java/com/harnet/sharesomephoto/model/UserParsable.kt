package com.harnet.sharesomephoto.model

import android.util.Log
import com.harnet.sharesomephoto.model.User
import com.parse.LogInCallback
import com.parse.ParseUser
import com.parse.SignUpCallback

interface UserParsable {

    //add new user
    fun addUser(user: User){
        val parseUser = ParseUser()

        //create a new user
        parseUser.username = user.name
        parseUser.setPassword(user.password)
        parseUser.email = user.email

        // sign in user automatically, till login functionality will be implemented
        parseUser.signUpInBackground {e ->
            if(e == null){
                Log.i("tweet", "addUser: add and sign up successfully")
                return@signUpInBackground
            }else{
                Log.i("tweet", "addUser: smth wrong with sign in" + e.printStackTrace())
                return@signUpInBackground
            }
        }
    }

    //log in
    fun logIn(userName: String, userPassword: String){
        ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
//            if (user != null) {
//            } else {
//                e.printStackTrace()
//            }
        })
    }

    //check if the user logged in
    fun isLoggedGetUser(): ParseUser? {
        if(ParseUser.getCurrentUser() != null){
            Log.i("tweet", "isLogged: you logged as: ${ParseUser.getCurrentUser().username}")
        }else{
            Log.i("tweet", "isLogged: ${ParseUser.getCurrentUser()}")
        }
        return ParseUser.getCurrentUser()
    }

    //log out
    fun logOut(){
        Log.i("tweet", "logOut: log out user: ${ParseUser.getCurrentUser().username}")
        return ParseUser.logOut()
    }
}