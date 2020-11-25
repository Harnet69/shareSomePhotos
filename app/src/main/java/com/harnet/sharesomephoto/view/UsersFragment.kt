package com.harnet.sharesomephoto.view

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.adapter.UsersAdapter
import com.harnet.sharesomephoto.databinding.UsersFragmentBinding
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.viewModel.UsersViewModel

class UsersFragment : Fragment() {
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var dataBinding: UsersFragmentBinding

    private lateinit var viewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.users_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersAdapter = UsersAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        viewModel.refresh()

        dataBinding.usersList.apply {
            layoutManager = LinearLayoutManager(context)
            //Fix blinking RecyclerView
            usersAdapter.setHasStableIds(true)
            //
            adapter = usersAdapter
        }
//
        // add separation line between items
        dataBinding.usersList.addItemDecoration(
            DividerItemDecoration(
                dataBinding.usersList.context,
                DividerItemDecoration.VERTICAL
            )
        )

        // Swiper refresh listener(screen refreshing process)
        dataBinding.refreshLayout.setOnRefreshListener {
            dataBinding.usersList.visibility = View.GONE
            dataBinding.listErrorTextView.visibility = View.GONE
            dataBinding.loadingViewProgressBar.visibility = View.VISIBLE
            viewModel.refresh()
            dataBinding.refreshLayout.isRefreshing = false // disappears little spinner on the top

        }

        // Add/change user image
        dataBinding.addImageBtn.setOnClickListener {
            (activity as MainActivity).appPermissions.imagePermissionService.checkPermission()
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mUsers.observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                dataBinding.usersList.visibility = View.VISIBLE
                usersAdapter.updateUsersList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsArticleLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                dataBinding.listErrorTextView.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                dataBinding.loadingViewProgressBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    dataBinding.listErrorTextView.visibility = View.GONE
                    dataBinding.usersList.visibility = View.GONE
                }
            }
        })

    }

    // method is called when activity get a result of user Image permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            openImageChooser(activity as Activity)
        }
    }
}