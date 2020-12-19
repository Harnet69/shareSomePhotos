package com.harnet.sharesomephoto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.MaterialToolbar
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.service.OnSingleClickListenerService
import com.harnet.sharesomephoto.view.FeedsFragmentDirections
import com.harnet.sharesomephoto.view.ProfileFragmentDirections
import com.harnet.sharesomephoto.view.UserDetailsFragmentDirections
import com.harnet.sharesomephoto.view.UsersFragmentDirections
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import org.json.JSONArray

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

// set an image to a menu item
@BindingAdapter("android:loadImageToMaterialToolbar")
fun loadImageToMaterialToolbar(materialToolbar: MaterialToolbar, imageUrl: String?) {
    val options = RequestOptions()
        .placeholder(getProgressDrawable(materialToolbar.context))
        .error(R.drawable.profile_ico)

    Glide.with(materialToolbar.context)
        .setDefaultRequestOptions(options)
        .asBitmap()
        .load(imageUrl)
        .apply(RequestOptions().override(120, 120))
        .circleCrop()
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                bitmapImg: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                val imgBitmap = BitmapDrawable(Resources.getSystem(), bitmapImg)
                materialToolbar.navigationIcon = imgBitmap
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
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
        imageUrl?.let { url ->
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
fun goToUserDetails(view: View, userId: String?) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener {
        val isUserProfile = userId == ParseUser.getCurrentUser().objectId
        userId?.let {
            var action: NavDirections? = null

            when (view.tag.toString()) {
                "usersFragment" -> {
                    action = if (!isUserProfile) {
                        UsersFragmentDirections.actionUsersFragmentToUserDetailsFragment(userId)
                    }else{
                        UsersFragmentDirections.actionUsersFragmentToProfileFragment()
                    }
                }
                "feedsFragment" -> {
                    action = if (!isUserProfile) {
                        FeedsFragmentDirections.actionFeedsFragmentToUserDetailsFragment(userId)
                    }else{
                        FeedsFragmentDirections.actionFeedsFragmentToProfileFragment()
                    }
                }
            }

            action?.let { Navigation.findNavController(view).navigate(action) }
        }
    }
}

// go to Users page with users list
@BindingAdapter("android:goToUsers")
fun goToUsers(view: View, following: ArrayList<String>?) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener {
        when (view.tag.toString()) {
            "profileDetailsFragment" -> {
                val action = ProfileFragmentDirections.actionProfileFragmentToUsersFragment(
                    following?.toTypedArray()
                )
                Navigation.findNavController(view).navigate(action)
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
fun loadUserNameById(textView: TextView, userId: String) {
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

// convert json to array
fun <T> jsonToArray(jArray: JSONArray?): MutableList<T> {
    val convertedArray = mutableListOf<T>()

    if (jArray != null) {
        for (i in 0 until jArray.length()) {
            convertedArray.add(jArray[i] as T)
        }
    }
    return convertedArray
}