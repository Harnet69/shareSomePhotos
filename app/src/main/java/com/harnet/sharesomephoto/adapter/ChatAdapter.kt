package com.harnet.sharesomephoto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemChatBinding
import com.parse.ParseUser

class ChatAdapter(private var chatList: ArrayList<com.harnet.sharesomephoto.model.Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    //for updating information from a backend
    fun updateUsersList(newMsgList: List<com.harnet.sharesomephoto.model.Message>) {
        chatList.clear()
        chatList.addAll(newMsgList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemChatBinding>(
            inflater,
            R.layout.item_chat,
            parent,
            false
        )
        return ChatAdapter.ChatViewHolder(view)
    }

    class ChatViewHolder(var view: ItemChatBinding) : RecyclerView.ViewHolder(view.root)

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.view.msg = chatList[position]
    }
}