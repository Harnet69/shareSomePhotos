package com.harnet.sharesomephoto.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

class ChatViewModel : ViewModel() {

    val mUser = MutableLiveData<User>()
    val mIsUserLoadError = MutableLiveData<Boolean>()

    fun refresh(){
        Log.i("RefreshChat", "refresh: ")
    }

    fun getUserById(userId: String) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereEqualTo("objectId", userId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    val retrievedUser = objects[0]
                    val userForBind = User(retrievedUser.username, "", "")
                    userForBind.profileImgUrl = retrievedUser["profileImg"].toString()
                    mUser.value = userForBind
                } else {
                    mIsUserLoadError.postValue(false)
                }
            } else {
                mIsUserLoadError.postValue(true)
            }
        })
    }
}