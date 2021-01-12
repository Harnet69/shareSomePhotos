package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.ChatItem
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.launch

class ChatsListViewModel(application: Application) : BaseViewModel(application) {
    val mChatsList = MutableLiveData<ArrayList<ChatItem>>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mIsChatsLoadError = MutableLiveData<Boolean>()

    fun refresh(chatUsersListIds: Array<String>) {
        getChatsFromParseServer(chatUsersListIds)
    }

    // retrieve images
    private fun retrieveChats(chatsFromParse: ArrayList<ChatItem>) {
        // set received list to observable mutable list
        mChatsList.postValue(chatsFromParse)
        // switch off error message
        mIsChatsLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    private fun getChatsFromParseServer(chatUsersListIds: Array<String>) {
        launch {
            val chatsList = arrayListOf<ChatItem>()

            for (userId in chatUsersListIds) {
                val query1 = ParseQuery<ParseObject>("Message")
                query1.whereEqualTo("sender", ParseUser.getCurrentUser().objectId)
                query1.whereEqualTo("recipient", userId)

                val query2 = ParseQuery<ParseObject>("Message")
                query2.whereEqualTo("sender", userId)
                query2.whereEqualTo("recipient", ParseUser.getCurrentUser().objectId)

                val queries = arrayListOf<ParseQuery<ParseObject>>()
                queries.add(query1)
                queries.add(query2)

                val query = ParseQuery.or(queries)
                query.orderByDescending("createdAt")

                query.getFirstInBackground { `object`, e ->
                    if(e == null){
                        val lastMsg = `object`?.get("text").toString()
                        Log.i("ListOfCHats", "$userId : $lastMsg ")
                        val prevChats = arrayListOf<ChatItem>()
                        mChatsList.value?.let { prevChats.addAll(it) }
                        prevChats.add(ChatItem(userId, lastMsg))
                        mChatsList.value = prevChats
                    }else{
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
