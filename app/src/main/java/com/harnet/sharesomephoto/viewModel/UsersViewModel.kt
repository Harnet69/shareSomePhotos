package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

class UsersViewModel(application: Application) : BaseViewModel(application) {
    val mUsers = MutableLiveData<List<User>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    fun refresh() {
        getUsersFromParseServer()
    }

    // retrieve users
    private fun retrieveUsers(usersFromParse: List<User>) {
        // set received list to observable mutable list
        mUsers.postValue(usersFromParse)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    private fun getUsersFromParseServer(){
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        // exclude user of this device
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().username)
        // sort by name anciently
        query.addAscendingOrder("username")
        query.findInBackground(FindCallback { objects, e ->
            val usersFromParse = mutableListOf<User>()

            if(e == null){
                if(objects.isNotEmpty()){
                    for(user in objects){
                        usersFromParse.add(User(user.username, "", ""))
                    }

                    retrieveUsers(usersFromParse)
                }else{
                    mIsLoading.postValue(false)
                    mIsArticleLoadError.postValue(false)
                    Toast.makeText(getApplication(), "No users here yet", Toast.LENGTH_LONG).show()
                }
            }else{
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsArticleLoadError.postValue(true)
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}