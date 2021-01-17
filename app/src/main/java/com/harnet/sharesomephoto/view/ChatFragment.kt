package com.harnet.sharesomephoto.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.adapter.ChatAdapter
import com.harnet.sharesomephoto.databinding.ChatFragmentBinding
import com.harnet.sharesomephoto.service.SoundService
import com.harnet.sharesomephoto.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.chat_fragment.*


class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel

    private lateinit var dataBinding: ChatFragmentBinding

    private lateinit var chatAdapter: ChatAdapter

    private var userId: String = ""

    // Repeating
    private var mInterval: Int = 5000 // 5 seconds by default, can be changed later

    private var mHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false)
        chatAdapter = ChatAdapter(arrayListOf())

        //repeat
        mHandler =  Handler()
        startRepeatingTask()

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = ChatFragmentArgs.fromBundle(it).userId
        }

        observeModel()

        chat_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        viewModel.getUserById(userId)

        //get text from Input text view
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
                chatList.let {
                    loadingView_ProgressBar_chatFragment.visibility = View.INVISIBLE
                    chat_list.visibility = View.VISIBLE
                    chatAdapter.updateUsersList(chatList)
                    chat_list.smoothScrollToPosition(chatList.size)
                }
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

    //repeating
    private var mStatusChecker: Runnable? = object : Runnable {
        override fun run() {
            try {
                //this function can change value of mInterval.
//                updateStatus()
                // refresh a chat
                viewModel.getChatList(userId)
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler?.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun startRepeatingTask() {
        mStatusChecker!!.run()
    }

    private fun stopRepeatingTask() {
        mHandler?.removeCallbacks(mStatusChecker!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }
}