package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.lookatthis.model.User
import com.harnet.lookatthis.model.UserParsable
import com.parse.ParseUser
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : BaseViewModel(application), UserParsable {
    val mIsUserExists = MutableLiveData<Boolean>()
    val mIsUserLogged = MutableLiveData<Boolean>()

    //TODO implement MutableList IsUserLogged, isUserExists

    fun addNewUser(newUser: User){
//        launch {
            val parseUser = ParseUser()

            //create a new user
            parseUser.username = newUser.name
            parseUser.setPassword(newUser.password)
            parseUser.email = newUser.email

            // sign in user automatically, till login functionality will be implemented
            parseUser.signUpInBackground {e ->
                if(e == null){
                    Log.i("tweet", "addUser: add and sign up successfully")
                    mIsUserExists.setValue(false)
                }else{
                    Log.i("tweet", "addUser: smth wrong with sign in + e.printStackTrace()")
                    mIsUserExists.setValue(true)
                }
            }
//        }
    }
}