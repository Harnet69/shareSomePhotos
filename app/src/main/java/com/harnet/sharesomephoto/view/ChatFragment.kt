package com.harnet.sharesomephoto.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ChatFragmentBinding
import com.harnet.sharesomephoto.viewModel.ChatViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.chat_fragment.*


class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel

    private lateinit var dataBinding: ChatFragmentBinding

    private var chatListAdapter: ArrayAdapter<String>? = null

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

        // fix problem with a swipe and listView bug
        fixSwipe()

        // Swiper refresh listener(screen refreshing process)
        refreshLayout_chatFragment.setOnRefreshListener {
            chat_list.visibility = View.GONE
            listError_TextView_chatFragment.visibility = View.GONE
            loadingView_ProgressBar_chatFragment.visibility = View.VISIBLE
            viewModel.getChatList(userId)

            refreshLayout_chatFragment.isRefreshing = false // disappears little spinner on the top
        }


        viewModel.getUserById(userId)

        //TODO Get text from Input text view
        sendingMsg(userId)
        viewModel.getChatList(userId)
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
                val chatListMsgs = arrayListOf<String>()

                for (msg in chatList) {
                    if (msg.senderId == ParseUser.getCurrentUser().objectId.toString()) {
                        chatListMsgs.add("> " + msg.text)
                    } else {
                        chatListMsgs.add(msg.text)
                    }
                }
                chatListAdapter = context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_list_item_1,
                        chatListMsgs
                    )
                }
                chat_list.adapter = chatListAdapter
            }
        })

        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                loadingView_ProgressBar_chatFragment.visibility = View.VISIBLE
            } else {
                loadingView_ProgressBar_chatFragment.visibility = View.INVISIBLE
            }
        })

        viewModel.mIsLoadingError.observe(viewLifecycleOwner, Observer { e ->
            if (e) {
                listError_TextView_chatFragment.visibility = View.VISIBLE
            }
        })

        viewModel.mIsMsgSentErrorMsg.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun sendingMsg(recipientId: String){
        send_msg_btn.setOnClickListener {
            val userMsg = message_text_field.text.toString()
            if(userMsg.isNotEmpty()){
                viewModel.sendMessage(userMsg, recipientId)
                message_text_field.text?.clear()
            }else{
                Toast.makeText(context, "Empty message!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // allows to correct work of a swiper
    private fun fixSwipe(){
        chat_list.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (chat_list.getChildAt(0) != null) {
                    refreshLayout_chatFragment.isEnabled = chat_list.firstVisiblePosition == 0 && chat_list.getChildAt(
                        0
                    ).top == 0
                }
            }
        })
    }
}