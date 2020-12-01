package com.harnet.sharesomephoto.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.databinding.ItemUserBinding
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery

class UsersAdapter(private var usersList: ArrayList<User>): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(){

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
        //TODO change User model to keep user Image as well
//        setProfileImage(usersList[position].profileImgId, holder.view.userImageImageViewProfile)
    }

    // Fix blinking RecyclerView
//    override fun getItemId(position: Int): Long {
//        return usersList.get(position).userId.toLong()
//    }

    //TODO move to binding
    private fun setProfileImage(profileImgId: String, userImageView: ImageView) {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("objectId", profileImgId)

        query.findInBackground(FindCallback { objects, parseObjectError ->
            if (parseObjectError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        userImageView.loadImage(
                            parseFile.url,
                            getProgressDrawable(userImageView.context)
                        )
                    }
                } else {
//                    Toast.makeText(userImageView.context, "No profile image", Toast.LENGTH_SHORT).show()
                }
            } else {
                parseObjectError.printStackTrace()
                Toast.makeText(userImageView.context, parseObjectError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}