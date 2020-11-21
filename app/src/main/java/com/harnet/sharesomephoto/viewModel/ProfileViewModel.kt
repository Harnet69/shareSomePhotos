package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.lookatthis.model.User
import com.harnet.lookatthis.model.UserParsable
import com.parse.ParseUser
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : BaseViewModel(application) {
    val mIsUserExists = MutableLiveData<Boolean>()
    val mIsUserLogged = MutableLiveData<Boolean>()

    // sign Up a new user
    fun addNewUser(newUser: User){
        mIsUserExists.setValue(false)

            val parseUser = ParseUser()

            //create a new user
            parseUser.username = newUser.name
            parseUser.setPassword(newUser.password)
            parseUser.email = newUser.email

            // sign in user automatically, till login functionality will be implemented
            parseUser.signUpInBackground {e ->
                if(e == null){
                    Toast.makeText(getApplication(), "Signed Up Successfully", Toast.LENGTH_SHORT).show()
                    mIsUserExists.setValue(false)
                    isLogged()
                }else{
                    Log.i("tweet", "addUser: smth wrong with sign in" + e.printStackTrace())
                    Toast.makeText(getApplication(), "User exists", Toast.LENGTH_SHORT).show()
                    mIsUserExists.setValue(true)
                }
            }
    }

    //check if the user logged in
    fun isLogged(): ParseUser?{
        if(ParseUser.getCurrentUser() != null){
            mIsUserLogged.setValue(true)
            Log.i("tweet", "isLogged: you logged as: ${ParseUser.getCurrentUser().username}")
        }else{
            mIsUserLogged.setValue(false)
            Log.i("tweet", "isLogged: ${ParseUser.getCurrentUser()}")
        }
        return ParseUser.getCurrentUser()
    }
}