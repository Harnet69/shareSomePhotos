package com.harnet.sharesomephoto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.service.OnSingleClickListenerService
import com.harnet.sharesomephoto.view.FeedsFragmentDirections
import com.harnet.sharesomephoto.view.ProfileFragmentDirections
import com.harnet.sharesomephoto.view.UserDetailsFragmentDirections
import com.harnet.sharesomephoto.view.UsersFragmentDirections
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

fun openImageChooser(activity: Activity) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    activity.startActivityForResult(Intent.createChooser(intent, "Select image"), 123)
}

//little loading spinner for image loading
fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

//extension for auto loading image of ImageView element using Glide library
fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.profile_ico)
//        .transform(CenterCrop(), RoundedCorners(50))
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)// this - extended ImageView class
}

// load image from URL
@BindingAdapter("android:bindImageUrl")
fun loadBindingImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

//extension for a user title changing
fun Fragment.setActivityTitle(@StringRes id: Int) {
    (activity as AppCompatActivity?)!!.supportActionBar?.title = getString(id)
}

fun Fragment.setActivityTitle(title: String) {
    (activity as AppCompatActivity?)!!.supportActionBar?.title = title
}

// go to Image page from any fragment, DON'T FORGET TO SET tag on Image View for fragment recognizing!!!
@BindingAdapter("android:goToImagePage")
fun goToImagePage(view: ImageView, imageUrl: String?) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener { imageView ->
        imageUrl?.let {url ->
                when (imageView.tag.toString()) {
                    "profileFragment" -> {
                        val action = ProfileFragmentDirections.actionProfileFragmentToImageFragment(url)
                        Navigation.findNavController(imageView).navigate(action)
                    }
                    "imageFragment" -> {
                        val action = FeedsFragmentDirections.actionFeedsFragmentToImageFragment(url)
                        Navigation.findNavController(imageView).navigate(action)
                    }
                    "userDetailsFragment" -> {
                        val action =
                        UserDetailsFragmentDirections.actionUserDetailsFragmentToImageFragment(url)
                        Navigation.findNavController(imageView).navigate(action)
                    }
            }
        }
    }
}

// go to user details page
@BindingAdapter("android:goToUserDetails")
fun goToUserDetails(view: View, username: String?) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener {
        username?.let {
            when (view.tag.toString()) {
                "usersFragment" -> {
                    val action =
                        UsersFragmentDirections.actionUsersFragmentToUserDetailsFragment(username)
                    Navigation.findNavController(view).navigate(action)
                }
                "feedsFragment" -> {
                    val action2 =
                        FeedsFragmentDirections.actionFeedsFragmentToUserDetailsFragment(username)
                    Navigation.findNavController(view).navigate(action2)
                }
            }
        }
    }
}

//get image from Image Library of device
fun convertImageDataToBitmap(activity: Activity, data: Intent?): Bitmap? {
    val selectedImage = data?.data
    var bitmap: Bitmap? = null

    try {
        bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return bitmap
}

@BindingAdapter("android:bindUserName")
fun loadUserNameById(textView: TextView, userId: String){
    val query: ParseQuery<ParseUser> = ParseUser.getQuery()
    // exclude user of this device
    query.whereEqualTo("objectId", userId)
    query.findInBackground(FindCallback { objects, e ->
        if (e == null) {
            if (objects.isNotEmpty()) {
                textView.text = objects[0].username
            }
        } else {
            e.printStackTrace()
        }
    })
}