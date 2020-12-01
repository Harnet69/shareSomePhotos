package com.harnet.sharesomephoto.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.adapter.FeedsAdapter
import com.harnet.sharesomephoto.databinding.FeedsFragmentBinding
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.FeedsViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.feeds_fragment.*

class FeedsFragment : Fragment() {
    private lateinit var feedsAdapter: FeedsAdapter
    private lateinit var dataBinding: FeedsFragmentBinding
    private lateinit var viewModel: FeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.feeds_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setActivityTitle("Users feed")
        feedsAdapter = FeedsAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)

        viewModel.refresh()

        feeds_list_FeedsFragment.apply {
            layoutManager = LinearLayoutManager(context)

            //Fix blinking RecyclerView
            feedsAdapter.setHasStableIds(true)

            adapter = feedsAdapter
        }

        // add separation line between items
        feeds_list_FeedsFragment.addItemDecoration(
            DividerItemDecoration(
                feeds_list_FeedsFragment.context,
                DividerItemDecoration.VERTICAL
            )
        )

        // redirect to Profile if a user not logged
        if (ParseUser.getCurrentUser() == null) {
            goToProfile()
        }

        // switch on a menu
        setHasOptionsMenu(true)

        // Add/change user image
        addImage_btn.setOnClickListener {
            if (this.context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser(activity as Activity)
            } else {
                (activity as MainActivity).appPermissions.imagesService.checkPermission()
            }
        }

        // Swiper refresh listener(screen refreshing process)
        refreshLayout_feedsFragment.setOnRefreshListener {
            listError_TextView_feedsFragment.visibility = View.GONE
            loadingView_ProgressBar_feedsFragment.visibility = View.VISIBLE
            viewModel.refresh()
            refreshLayout_feedsFragment.isRefreshing = false // disappears little spinner on the top

        }

        observeViewModel()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    // click listener for menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.users -> {
                goToUsers()
            }
            R.id.profile -> {
                goToProfile()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mFeeds.observe(viewLifecycleOwner, Observer { images ->
            images?.let {imagesList ->
                feeds_list_FeedsFragment.visibility = View.VISIBLE
                feedsAdapter.updateFeedsList(imagesList)
            }
        })

        // make error TextViewVisible
        viewModel.mIsImageLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                listError_TextView_feedsFragment.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingView_ProgressBar_feedsFragment.visibility =
                    if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    listError_TextView_feedsFragment.visibility = View.GONE
                    feeds_list_FeedsFragment.visibility = View.GONE
                }
            }
        })
    }

    // redirect to the Profile page
    private fun goToProfile() {
        view?.let {
            Navigation.findNavController(it)
                .navigate(FeedsFragmentDirections.actionFeedsFragmentToProfileFragment())
        }
    }

    // redirect to the Users list
    private fun goToUsers() {
        view?.let {
            Navigation.findNavController(it)
                .navigate(FeedsFragmentDirections.actionFeedsFragmentToUsersFragment())
        }
    }

    // method is called when activity get a result of user Image permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        if (permissionGranted) {
            openImageChooser(activity as Activity)
        }
    }
}