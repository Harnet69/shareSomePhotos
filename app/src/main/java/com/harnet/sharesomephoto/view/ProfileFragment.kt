package com.harnet.sharesomephoto.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ProfileFragmentBinding
import com.harnet.sharesomephoto.viewModel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var dataBinding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // DataBinding
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        view.findViewById<Button>(R.id.sign_up_btn).setOnClickListener {
                Toast.makeText(context, "Click on add", Toast.LENGTH_SHORT).show()
        }

        observeModel()
    }

    private fun observeModel() {
        viewModel.mIsUserExists.observe(viewLifecycleOwner, Observer { isExists ->
            Log.i("tweet", "observeModel: ")
            if(isExists){
                Toast.makeText(context, "User exists", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Log In", Toast.LENGTH_SHORT).show()
            }
        })
    }
}