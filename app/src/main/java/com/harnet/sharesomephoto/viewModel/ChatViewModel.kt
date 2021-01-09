package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.sharesomephoto.model.Message
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : BaseViewModel(application) {

    val mUser = MutableLiveData<User>()
    val mIsUserLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mChatList = MutableLiveData<List<Message>>()

    fun refresh(){
        getChatList()
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
                    mIsLoading.value = false
                    mIsUserLoadError.postValue(true)
                }
            } else {
                mIsLoading.postValue(false)
                mIsUserLoadError.postValue(true)
            }
        })
    }

    private fun getChatList(){
        launch {
            delay(3000L)
            mIsLoading.value = false
            mChatList.value = listOf<Message>(Message())
            Log.i("RefreshChat", "refresh: ")
        }
    }
}