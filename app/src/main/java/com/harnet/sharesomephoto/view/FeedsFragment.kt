package com.harnet.sharesomephoto.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.viewModel.FeedsViewModel

class FeedsFragment : Fragment() {

    companion object {
        fun newInstance() = FeedsFragment()
    }

    private lateinit var viewModel: FeedsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}