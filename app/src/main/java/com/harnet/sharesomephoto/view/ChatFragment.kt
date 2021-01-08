package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ChatFragmentBinding
import com.harnet.sharesomephoto.viewModel.ChatViewModel

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel

    private lateinit var dataBinding: ChatFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        viewModel.refresh(userId)
    }

    private fun observeModel() {
        viewModel.mUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                dataBinding.user = user
            }
        })
    }

}