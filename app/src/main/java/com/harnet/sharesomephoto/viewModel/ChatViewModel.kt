package com.harnet.sharesomephoto.viewModel

import android.app.Application
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
    val mIsMsgSentMsg = MutableLiveData<String>()

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

        message.saveInBackground(SaveCallback {e ->
            if(e == null){
                mIsMsgSentMsg.value = "Message sent"
            }else{
                //TODO something goes wrong
                mIsMsgSentMsg.value = e.localizedMessage
            }
        })
    }

    fun getChatList(userId: String) {
        launch {
//            delay(2000L)
//            mIsLoading.value = false
//            mChatList.value = listOf<Message>(
//                Message("123", "123", "Hello"),
//                Message(ParseUser.getCurrentUser().objectId, "123", "Good bye!")
//            )
            //TODO implement getting messages from server
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
                            val msgCreatedAt = msg.get("createdAt")
                            // TODO think about Date format
                            msgsList.add(Message(msgSender, msgRecipient, msgText))
                        }

                        mChatList.value = msgsList
                    }
                }else{
                    mIsLoadingError.value = true
                }
            })
        }
    }
}