package com.harnet.sharesomephoto.util

import android.util.Log
import com.parse.ParseUser

//check if the user logged in
fun isLogged(): ParseUser? {
    if(ParseUser.getCurrentUser() != null){
        Log.i("tweet", "isLogged: you logged as: ${ParseUser.getCurrentUser().username}")
    }else{
        Log.i("tweet", "isLogged: ${ParseUser.getCurrentUser()}")
    }
    return ParseUser.getCurrentUser()
}