package com.harnet.sharesomephoto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemUserBinding
import com.harnet.sharesomephoto.model.User

class UsersAdapter(private var usersList: ArrayList<User>): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    //for updating information from a backend
    fun updateUsersList(newUsersList: List<User>) {
        // get new parsed articles
        if (newUsersList.isNotEmpty()) {
            val newCastedUsersList = newUsersList as ArrayList<User>

        usersList.clear()
        usersList.addAll(newCastedUsersList)
        //reset RecycleView and recreate a list
        notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // elements of the list transforms into views. DataBinding approach
        val view = DataBindingUtil.inflate<ItemUserBinding>(
            inflater,
            R.layout.item_user,
            parent,
            false
        )
        return UsersViewHolder(view)
    }

    class UsersViewHolder(var view: ItemUserBinding): RecyclerView.ViewHolder(view.root)

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        // bind data to xml variable view
        holder.view.user = usersList[position]
    }

    // TODO fix blinking RecyclerView
//    override fun getItemId(position: Int): Long {
//        return usersList.get(position).id.toLong()
//    }

}