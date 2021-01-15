package com.harnet.sharesomephoto.viewModel

import android.app.Activity
import android.app.Application
import android.util.ArraySet
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.ChatItem
import com.harnet.sharesomephoto.model.Message
import com.harnet.sharesomephoto.service.SoundService
import com.harnet.sharesomephoto.util.markChatsBtnAsHasNewMsg
import com.harnet.sharesomephoto.view.MainActivity
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.launch

class ChatsListViewModel(application: Application) : BaseViewModel(application) {
    val mChatUsersList = MutableLiveData<ArraySet<String>>()
    val mChatsList = MutableLiveData<ArrayList<ChatItem>>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mIsChatsLoadError = MutableLiveData<Boolean>()

    val soundService = SoundService(getApplication())
    private var isIncomingSoundPlayed = false

    fun refresh(activity: Activity, chatUsersListIds: ArraySet<String>) {
        getChatsFromParseServer(activity, chatUsersListIds)
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

    private fun getChatsFromParseServer(activity: Activity, chatUsersListIds: ArraySet<String>) {
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
                        val msgSender = `object`?.get("sender").toString()
                        val msgRecipient = `object`?.get("recipient").toString()
                        var msgText = ""
                        msgText = if(msgSender == ParseUser.getCurrentUser().objectId) {
                            "You: " + `object`?.get("text").toString()
                        }else{
                            `object`?.get("text").toString()
                        }
                        val msgDate = `object`?.createdAt
                        val isRead = `object`.getBoolean("isRead")

                        // play incoming message sound
                        if (msgSender != ParseUser.getCurrentUser().objectId && !isRead && !isIncomingSoundPlayed) {
                            soundService.playSound("new_msg")
                            isIncomingSoundPlayed = true
                            // mark chats button
                            markChatsBtnAsHasNewMsg(activity, true)
                        }

                        val prevChats = arrayListOf<ChatItem>()
                        mChatsList.value?.let { prevChats.addAll(it) }
                        prevChats.add(ChatItem(userId, Message(msgSender, msgRecipient, msgText, msgDate, isRead)))
                        mChatsList.value = prevChats
                    }else{
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // get users have a chat with
    fun getChatUsersId() {
        launch {
            val query1 = ParseQuery<ParseObject>("Message")
            query1.whereEqualTo("sender", ParseUser.getCurrentUser().objectId)

            val query2 = ParseQuery<ParseObject>("Message")
            query2.whereEqualTo("recipient", ParseUser.getCurrentUser().objectId)

            val queries = arrayListOf<ParseQuery<ParseObject>>()
            queries.add(query1)
            queries.add(query2)

            val query = ParseQuery.or(queries)

            query.findInBackground { objects, e ->
                if (e == null) {
                    val chatUsersIds = ArraySet<String>()

                    for (i in objects.indices) {
                        if (objects[i].get("sender").toString() == ParseUser.getCurrentUser().objectId) {
                            chatUsersIds.add(objects[i].get("recipient").toString())
                        }
                        if (objects[i].get("recipient").toString() == (ParseUser.getCurrentUser().objectId)) {
                            chatUsersIds.add(objects[i].get("sender").toString())
                        }
                    }

                    mChatsList.value = arrayListOf()
                    mChatUsersList.value = chatUsersIds
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}
