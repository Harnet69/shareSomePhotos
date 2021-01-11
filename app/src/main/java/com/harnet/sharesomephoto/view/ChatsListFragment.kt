package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.harnet.sharesomephoto.viewModel.ChatsListViewModel
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ChatsListFragmentBinding

class ChatsListFragment : Fragment() {
    private lateinit var viewModel: ChatsListViewModel
    private lateinit var dataBinding: ChatsListFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.chats_list_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatsListViewModel::class.java)
    }
}