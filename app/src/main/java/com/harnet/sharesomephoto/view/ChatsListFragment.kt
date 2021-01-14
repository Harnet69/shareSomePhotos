package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.util.ArraySet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.sharesomephoto.viewModel.ChatsListViewModel
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.adapter.ChatsListAdapter
import com.harnet.sharesomephoto.databinding.ChatsListFragmentBinding
import kotlinx.android.synthetic.main.chats_list_fragment.*

class ChatsListFragment : Fragment() {
    private lateinit var viewModel: ChatsListViewModel

    private lateinit var dataBinding: ChatsListFragmentBinding

    private lateinit var chatsListAdapter: ChatsListAdapter

    private var chatUsersId = ArraySet<String>()

    // Repeating
    private var mInterval: Int = 5000 // 5 seconds by default, can be changed later

    private var mHandler: Handler? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.chats_list_fragment, container, false)
        chatsListAdapter = ChatsListAdapter(arrayListOf())

        viewModel = ViewModelProvider(this).get(ChatsListViewModel::class.java)

        //repeat
        mHandler =  Handler()

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fixed the bug with chats duplication
        viewModel.mChatsList.value = arrayListOf()

//        viewModel.getChatUsersId()

        startRepeatingTask()

        observeViewModel()

        chats_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatsListAdapter
        }
    }

    private fun observeViewModel() {
        // get chat users id list
        viewModel.mChatUsersList.observe(viewLifecycleOwner, Observer { chatUsersIdList ->
            chatUsersIdList?.let {
                chatUsersId.addAll(it)
                viewModel.refresh(it)
            }
        })

        // get users chats list
        viewModel.mChatsList.observe(viewLifecycleOwner, Observer { chatsList ->
            if (chatsList.size == chatUsersId.size) {
                val sortedList = chatsList.sortedBy { it.lastMsg.createAt }.reversed().toCollection(ArrayList())
                chatsListAdapter.updateChatsList(sortedList)
            }

        })
    }

    //repeating
    private var mStatusChecker: Runnable? = object : Runnable {
        override fun run() {
            try {
                //this function can change value of mInterval.
//                updateStatus()
                // refresh a chat
                    //TODO here is the problem
                viewModel.getChatUsersId()
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