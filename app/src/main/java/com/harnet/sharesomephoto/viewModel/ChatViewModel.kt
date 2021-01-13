package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.Message
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : BaseViewModel(application) {

    val mUser = MutableLiveData<User>()
    val mIsLoadingError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mChatList = MutableLiveData<List<Message>>()

    val mIsMsgSentErrorMsg = MutableLiveData<String>()

    private var msgsCounter = 0

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
                    mIsLoadingError.postValue(true)
                }
            } else {
                mIsLoading.postValue(false)
                mIsLoadingError.postValue(true)
            }
        })
    }

    fun sendMessage(msgTxt: String, recipientId: String){
        val message = ParseObject("Message")
        message.put("sender", ParseUser.getCurrentUser().objectId)
        message.put("recipient", recipientId)
        message.put("text", msgTxt)
        message.put("isRead", false)

        message.saveInBackground(SaveCallback {e ->
            if(e == null){
                getChatList(recipientId)
            }else{
                // if something goes wrong
                mIsMsgSentErrorMsg.value = e.localizedMessage
            }
        })
    }

    fun getChatList(userId: String) {
        launch {
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

            query.findInBackground(FindCallback { objects, e ->
                if(e == null){
                    if(objects.isNotEmpty()){
                        val msgsList = arrayListOf<Message>()

                        for(i in objects.indices){
                            val msg = objects[i]
                            val msgSender = msg.get("sender").toString()
                            val msgRecipient = msg.get("recipient").toString()
                            val msgText = msg.get("text").toString()
                            val msgCreatedAt = msg.createdAt
                            val isRead = msg.getBoolean("isRead")

                            msgsList.add(Message(msgSender, msgRecipient, msgText, msgCreatedAt, isRead))
                        }

                        // check if chat was changed
                        if(msgsCounter != msgsList.size){
                            mChatList.value = msgsList
                            // update counter
                            msgsCounter = msgsList.size
                        }

                    }else{
                        mIsLoading.value = false
                    }
                }else{
                    mIsLoadingError.value = true
                }
            })
        }
    }
}