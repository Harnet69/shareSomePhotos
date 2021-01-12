package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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

    private lateinit var chatUsersId: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.chats_list_fragment, container, false)
        chatsListAdapter = ChatsListAdapter(arrayListOf())
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatsListViewModel::class.java)

        arguments?.let {
            chatUsersId = ChatsListFragmentArgs.fromBundle(it).chatUsersList
        }

        viewModel.refresh(chatUsersId)

        observeViewModel()

        chats_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatsListAdapter
        }
    }

    private fun observeViewModel(){
        viewModel.mChatsList.observe(viewLifecycleOwner, Observer { chatsList ->
            if(chatsList.size == chatUsersId.size){
                //TODO implement notifying Adapter for changes
                Log.i("ListOfChats", "observeViewModel: $chatsList")

                chatsListAdapter.updateChatsList(chatsList)
            }
        })
    }
}