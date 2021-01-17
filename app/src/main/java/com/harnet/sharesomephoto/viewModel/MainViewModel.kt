package com.harnet.sharesomephoto.viewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.Message
import com.harnet.sharesomephoto.view.MainActivity
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val mNewMessages = MutableLiveData<ArrayList<Message>>()
    private var mIsNewMsg = false
    private var mIsNewMsgTrigger = MutableLiveData<Boolean>()

    fun refresh() {
        findNewMessage()
    }

    private fun findNewMessage() {
        val query = ParseQuery<ParseObject>("Message")
        query.whereEqualTo("recipient", ParseUser.getCurrentUser().objectId)
        query.whereEqualTo("isRead", false)

        query.findInBackground { objects, e ->
//            if(e != null){
//                Log.i("HasNewMessage", "$e: ")
//            }else{
//                Log.i("HasNewMessage", "$objects: ")
//            }

            if (e == null) {
                if (objects.isNotEmpty()) {
                    val oldNewMsgsList = ArrayList<Message>()
                    mNewMessages.value?.let { oldNewMsgsList.addAll(it) }
                    val newNewMsgsList = ArrayList<Message>()
                    for (newMsg in objects) {
                        val newMsg = Message(
                            newMsg.objectId,
                            newMsg.get("sender").toString(),
                            newMsg.get("recipient").toString(),
                            newMsg.get("text").toString(),
                            newMsg.get("createdAt") as Date?,
                            newMsg.get("isRead") as Boolean
                        )
                        //check if list which isn't empty contains the message
                        if (oldNewMsgsList.isNotEmpty()) {
                            for (oldMsg in oldNewMsgsList) {
                                Log.i("IsMsgNew", "${oldMsg.id} / ${newMsg.id}")
                                if (oldMsg.id != newMsg.id) {
                                    newNewMsgsList.add(newMsg)
                                    mIsNewMsg = true
                                }
                            }
                        } else {
                            newNewMsgsList.add(newMsg)
                            mIsNewMsg = true
                        }
                    }
                    // update newMessagesList if new message
                    if (mIsNewMsg) {
                        Log.i("HasNewMessage", "NewMessage ")
                        mIsNewMsgTrigger.value = true
                        mNewMessages.value = newNewMsgsList
                        mIsNewMsg = false
                    }else {
                        Log.i("HasNewMessage", "Any New message: ")
                        mIsNewMsgTrigger.value = false
                    }
                }
            }
        }
    }

    fun markChatsBtnAsHasNewMsg(activity: Activity, isNewMsg: Boolean) {
        val bottomNavigationView =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        val menu = bottomNavigationView.menu
        val chatsBtn = menu.findItem(R.id.chatsListFragment)

        if (isNewMsg) {
            //switch to different icons
            chatsBtn.setIcon(R.drawable.ic_chat_unread)
        } else {
            chatsBtn.setIcon(R.drawable.ic_chat)
        }
    }
}