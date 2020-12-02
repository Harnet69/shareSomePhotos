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
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.UserDetailsFragmentBinding
import com.harnet.sharesomephoto.util.setActivityTitle
import com.harnet.sharesomephoto.viewModel.UserDetailsViewModel
import kotlinx.android.synthetic.main.user_details_fragment.*

class UserDetailsFragment : Fragment() {
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var dataBinding: UserDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.user_details_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setActivityTitle("User details")
        viewModel = ViewModelProvider(this).get(UserDetailsViewModel::class.java)

        arguments?.let {
            val userId: String = UserDetailsFragmentArgs.fromBundle(it).userId
           viewModel.refresh(userId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                dataBinding.user = user
                user_detail_block_userDeskrFragment.visibility = View.VISIBLE
            }
        })

        // make error TextViewVisible
        viewModel.mIsUserLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                listError_TextView_UserDescr.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingView_ProgressBar_UserDescr.visibility =
                    if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    listError_TextView_UserDescr.visibility = View.GONE
                    user_detail_block_userDeskrFragment.visibility = View.GONE
                }
            }
        })
    }
}