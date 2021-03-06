package com.harnet.sharesomephoto.viewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.Message
import com.harnet.sharesomephoto.service.SoundService
import com.harnet.sharesomephoto.view.MainActivity
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : BaseViewModel(application) {
    val mNewMessages = MutableLiveData<ArrayList<Message>>()
    var mIsNewMsgTrigger = MutableLiveData<Boolean>()

    val soundService = SoundService(getApplication())

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
                        //do a sound, show a toast
                        //TODO Possibility to switch off a sound
                        soundService.playSound("new_msg")
                        showNewMsgToast(newLastMsg.senderId, newLastMsg.text)
                    } else {
                        if (lastMsg?.id != newLastMsg.id) {
                            soundService.playSound("new_msg")
                            showNewMsgToast(newLastMsg.senderId, newLastMsg.text)
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

    // get username by id and show message
    private fun showNewMsgToast(userId: String, msgText: String) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        // exclude user of this device
        query.whereEqualTo("objectId", userId)
        query.findInBackground { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    var cutMsg = ""
                    cutMsg = if(msgText.length > 20){
                        msgText.substring(0..20)
                    }else{
                        msgText
                    }
                    Toast.makeText(
                        getApplication(),
                        "${objects[0].username}: $cutMsg",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                e.printStackTrace()
            }
        }
    }
}