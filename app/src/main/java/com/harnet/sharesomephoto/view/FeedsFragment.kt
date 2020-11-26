package com.harnet.sharesomephoto.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.util.openImageChooser
import com.harnet.sharesomephoto.viewModel.FeedsViewModel
import com.parse.ParseUser
import kotlinx.android.synthetic.main.feeds_fragment.*

class FeedsFragment : Fragment() {

    private lateinit var viewModel: FeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feeds_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)

        viewModel.refresh()

        val userImages = viewModel.mImages.value
        if( userImages != null){
            for(image in userImages){
                Log.i("Availableimages", "${image.url}  :  ${image.userName}")
            }
        }

        // redirect to Profile if a user not logged
        if(ParseUser.getCurrentUser() == null){
            goToProfile()
        }

        // switch on a menu
        setHasOptionsMenu(true)

        // Add/change user image
        addImage_btn.setOnClickListener {
            (activity as MainActivity).appPermissions.imagesService.checkPermission()
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

    }

    // redirect to the Profile page
    private fun goToProfile(){
        view?.let {
            Navigation.findNavController(it)
                .navigate(FeedsFragmentDirections.actionFeedsFragmentToProfileFragment())
        }
    }

    // redirect to the Users list
    private fun goToUsers(){
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