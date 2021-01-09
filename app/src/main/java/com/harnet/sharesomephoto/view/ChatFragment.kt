package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ChatFragmentBinding
import com.harnet.sharesomephoto.viewModel.ChatViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.users_fragment.*
import java.util.stream.Collectors

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel

    private lateinit var dataBinding: ChatFragmentBinding

//    private lateinit var chatListAdapter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var userId: String

        arguments?.let {
            userId = ChatFragmentArgs.fromBundle(it).userId
        }

        observeModel()

        // Swiper refresh listener(screen refreshing process)
        refreshLayout_chatFragment.setOnRefreshListener {
            chat_list.visibility = View.GONE
            listError_TextView_chatFragment.visibility = View.GONE
            loadingView_ProgressBar_chatFragment.visibility = View.VISIBLE
            viewModel.refresh()

            refreshLayout_chatFragment.isRefreshing = false // disappears little spinner on the top
        }

        viewModel.getUserById(userId)
        viewModel.refresh()
    }

    private fun observeModel() {
        viewModel.mUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                dataBinding.user = it
            }
        })

        viewModel.mChatList.observe(viewLifecycleOwner, Observer { chatList ->
            if (!chatList.isNullOrEmpty()) {
                loadingView_ProgressBar_chatFragment.visibility = View.INVISIBLE
                chat_list.visibility = View.VISIBLE
                //TODO update ListView Adapter
//                for(msg in chatList){
//
//                }
                val chatListMsgs = arrayListOf<String>()

                for (msg in chatList) {
                    if (msg.senderId == ParseUser.getCurrentUser().objectId.toString()) {
                        chatListMsgs.add("> " + msg.text)
                    } else {
                        chatListMsgs.add(msg.text)
                    }
                }
                val chatListAdapter = context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_list_item_1,
                        chatListMsgs
                    )
                }
                chat_list.adapter = chatListAdapter
            }
        })

        viewModel.mIsUserLoadError.observe(viewLifecycleOwner, Observer { e ->
            if(e){
                listError_TextView_chatFragment.visibility = View.VISIBLE
            }
        })
    }
}