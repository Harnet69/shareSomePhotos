package com.harnet.sharesomephoto.viewModel

import android.app.Application
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

    fun refresh(chatUsersListIds: ArrayList<String>) {
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

    private fun getChatsFromParseServer(chatUsersListIds: ArrayList<String>) {

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
                query.orderByAscending("createdAt")

                query.getFirstInBackground { `object`, e ->
                    if(e == null){
                        val lastMsg = `object`?.get("text").toString()
                        chatsList.add(ChatItem(userId, lastMsg))
                    }else{
                        e.printStackTrace()
                    }
                }
            }

            mChatsList.value = chatsList
        }
    }
}