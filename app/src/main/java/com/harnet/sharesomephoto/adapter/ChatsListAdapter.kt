package com.harnet.sharesomephoto.adapter

import android.app.Activity
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemChatsListBinding
import com.harnet.sharesomephoto.model.ChatItem
import com.harnet.sharesomephoto.util.markChatsBtnAsHasNewMsg
import com.harnet.sharesomephoto.view.ChatsListFragmentDirections
import com.parse.ParseUser

class ChatsListAdapter(val activity: Activity, private var chatsList: ArrayList<ChatItem>):
RecyclerView.Adapter<ChatsListAdapter.ChatsListViewHolder>(){
    private var isAnyUnread = false

    //for updating information from a backend
    fun updateChatsList(newChatsList: ArrayList<ChatItem>) {
        chatsList.clear()
        chatsList.addAll(newChatsList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemChatsListBinding>(
            inflater,
            R.layout.item_chats_list,
            parent,
            false
        )
        return ChatsListAdapter.ChatsListViewHolder(view)
    }

    class ChatsListViewHolder(var view: ItemChatsListBinding): RecyclerView.ViewHolder(view.root)

    override fun onBindViewHolder(holder: ChatsListViewHolder, position: Int) {
        holder.view.chatUserBlock.setOnClickListener {
            val action = ChatsListFragmentDirections.actionChatsListFragmentToChatFragment(chatsList[position].chatUserId)
            Navigation.findNavController(it).navigate(action)

        }
        holder.view.chatItem = chatsList[position]

        if(!chatsList[position].lastMsg.isRead && chatsList[position].lastMsg.senderId != ParseUser.getCurrentUser().objectId) {
            holder.view.chatUserLastMsg.setTypeface(null, Typeface.BOLD)
            holder.view.chatUserIsRead.visibility = View.VISIBLE
//            markChatsBtnAsHasNewMsg(activity, true)
            isAnyUnread = true
        }else{
            // do not show blue dot of new message if it your message
            holder.view.chatUserIsRead.visibility = View.INVISIBLE
//            if(!isAnyUnread){markChatsBtnAsHasNewMsg(activity, false)}
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}