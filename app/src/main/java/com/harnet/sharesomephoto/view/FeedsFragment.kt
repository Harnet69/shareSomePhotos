package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.harnet.sharesomephoto.viewModel.FeedsViewModel
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding

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
        // switch on a menu
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)

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
            R.id.profile -> {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(FeedsFragmentDirections.actionFeedsFragmentToProfileFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {

    }
}