package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

class UserDetailsViewModel(application: Application) : BaseViewModel(application) {
    val mUser = MutableLiveData<User>()
    val mIsUserLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    fun refresh(username: String) {
        getUserFromParseServer(username)
    }

    // retrieve images
    private fun retrieveUser(userFromParse: User) {
        // set received list to observable mutable list
        mUser.postValue(userFromParse)
        // switch off error message
        mIsUserLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    private fun getUserFromParseServer(username: String) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereEqualTo("username", username)

        query.findInBackground(FindCallback { objects, e ->

            if (e == null) {
                if (objects.isNotEmpty()) {
                    val retrievedUser = objects[0]
                    retrieveUser(
                        User(
                            retrievedUser.username,
                            "",
                            "email test",
                            retrievedUser.get("profileImg").toString()
                        )
                    )
                    Log.i("RetrievedUser", "${retrievedUser.username} : ${retrievedUser.email}")
                } else {
                    mIsLoading.postValue(false)
                    mIsUserLoadError.postValue(false)
                    Toast.makeText(getApplication(), "No users here yet", Toast.LENGTH_LONG).show()
                }
            } else {
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsUserLoadError.postValue(true)
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}
