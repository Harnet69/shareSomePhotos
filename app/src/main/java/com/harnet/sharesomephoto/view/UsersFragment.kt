package com.harnet.sharesomephoto.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.adapter.UsersAdapter
import com.harnet.sharesomephoto.databinding.UsersFragmentBinding
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.UsersViewModel
import kotlinx.android.synthetic.main.users_fragment.*

class UsersFragment : Fragment() {
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var dataBinding: UsersFragmentBinding

    private lateinit var viewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.users_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var follow: String? = null
        this.setActivityTitle("List of users")
        usersAdapter = UsersAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        // get arguments if user came from Profile with users following list
        //TODO!!!!
        arguments?.let {
            follow = UsersFragmentArgs.fromBundle(it).follow
        }

        viewModel.refresh(follow)

        users_list_usersFragment.apply {
            layoutManager = LinearLayoutManager(context)
            //Fix blinking RecyclerView
            usersAdapter.setHasStableIds(true)
            adapter = usersAdapter
        }

        // add separation line between items
        users_list_usersFragment.addItemDecoration(
            DividerItemDecoration(
                users_list_usersFragment.context,
                DividerItemDecoration.VERTICAL
            )
        )

        // Swiper refresh listener(screen refreshing process)
        refreshLayout_usersFragment.setOnRefreshListener {
            users_list_usersFragment.visibility = View.GONE
            listError_TextView_usersFragment.visibility = View.GONE
            loadingView_ProgressBar_usersFragment.visibility = View.VISIBLE
            viewModel.refresh(follow)
            refreshLayout_usersFragment.isRefreshing = false // disappears little spinner on the top
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mUsers.observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                users_list_usersFragment.visibility = View.VISIBLE
                usersAdapter.updateUsersList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsArticleLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                listError_TextView_usersFragment.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingView_ProgressBar_usersFragment.visibility =
                    if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    listError_TextView_usersFragment.visibility = View.GONE
                    users_list_usersFragment.visibility = View.GONE
                }
            }
        })
    }
}