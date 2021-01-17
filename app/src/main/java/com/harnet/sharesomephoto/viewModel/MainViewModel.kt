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
    val mNewMessages = MutableLiveData<ArrayList<Message>>()
    var mIsNewMsgTrigger = MutableLiveData<Boolean>()

    private var lastMsg: Message? = null

    fun refresh() {
        findNewMessage()
    }

    private fun findNewMessage() {
        val query = ParseQuery<ParseObject>("Message")
        query.whereEqualTo("recipient", ParseUser.getCurrentUser().objectId)
        query.whereEqualTo("isRead", false)
        query.orderByAscending("createdAt")

        query.findInBackground { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    val newNewMsgsList = ArrayList<Message>()
                    for (newMsg in objects) {
                        val newMsgForAdd = Message(
                            newMsg.objectId,
                            newMsg.get("sender").toString(),
                            newMsg.get("recipient").toString(),
                            newMsg.get("text").toString(),
                            newMsg.get("createdAt") as Date?,
                            newMsg.get("isRead") as Boolean
                        )
                        newNewMsgsList.add(newMsgForAdd)
                    }
                    val newLastMsg = newNewMsgsList[newNewMsgsList.size - 1]
                    if (lastMsg == null) {
                        lastMsg = newLastMsg
                    } else {
                        if (lastMsg?.id != newLastMsg.id) {
                            //TODO Do a sound, show notification
                            lastMsg = newLastMsg
                        }
                    }
                    mIsNewMsgTrigger.value = true
                    mNewMessages.value = newNewMsgsList
                } else {
                    mIsNewMsgTrigger.value = false
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