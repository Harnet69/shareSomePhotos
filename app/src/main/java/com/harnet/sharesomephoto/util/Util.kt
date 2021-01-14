package com.harnet.sharesomephoto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.harnet.sharesomephoto.model.Fragments
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.service.OnSingleClickListenerService
import com.harnet.sharesomephoto.view.FeedsFragmentDirections
import com.harnet.sharesomephoto.view.ProfileFragmentDirections
import com.harnet.sharesomephoto.view.UserDetailsFragmentDirections
import com.harnet.sharesomephoto.view.UsersFragmentDirections
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import org.json.JSONArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.US
import kotlin.collections.ArrayList

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
//TODO add boolean argument for circular images
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
                // just for correct overriding
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
                Fragments.PROFILE.fragmentName -> {
                    val action = ProfileFragmentDirections.actionProfileFragmentToImageFragment(url)
                    Navigation.findNavController(imageView).navigate(action)
                }
                Fragments.IMAGE.fragmentName-> {
                    val action = FeedsFragmentDirections.actionFeedsFragmentToImageFragment(url)
                    Navigation.findNavController(imageView).navigate(action)
                }
                Fragments.USER_DETAILS.fragmentName -> {
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
                Fragments.USERS.fragmentName-> {
                    action = if (!isUserProfile) {
                        UsersFragmentDirections.actionUsersFragmentToUserDetailsFragment(userId)
                    }else{
                        UsersFragmentDirections.actionUsersFragmentToProfileFragment()
                    }
                }
                Fragments.FEEDS.fragmentName-> {
                    action = if (!isUserProfile) {
                        FeedsFragmentDirections.actionFeedsFragmentToUserDetailsFragment(userId)
                    }else{
                        FeedsFragmentDirections.actionFeedsFragmentToProfileFragment()
                    }
                }

                Fragments.PROFILE_DETAILS.fragmentName -> {
                    action = ProfileFragmentDirections.actionProfileFragmentToUserDetailsFragment(userId)
                }
            }

            action?.let { Navigation.findNavController(view).navigate(action) }
        }
    }
}

// go to Users page with users list
@BindingAdapter("android:goToUsers")
fun goToUsers(view: View, follow: ArrayList<String>?) {
    // prevent from going to empty list of following
    val isFollow = follow?.let { follow.size > 0}
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener {
        when (view.tag.toString()) {
            "profileDetailsFragmentFollowing" -> {
                if(isFollow == true) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToUsersFragment(
                        "following"
                    )
                    Navigation.findNavController(view).navigate(action)
                }else{
                    Toast.makeText(view.context, "No following yet", Toast.LENGTH_SHORT).show()
                }
            }

            "profileDetailsFragmentFollowers" -> {
                if(isFollow == true) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToUsersFragment(
                        "followers"
                    )
                    Navigation.findNavController(view).navigate(action)
                }else{
                    Toast.makeText(view.context, "Any followers yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// go to Users page with users list
@BindingAdapter("android:goToChatUsers")
fun goToChatUsers(view: View, chatUsers: ArrayList<String>?) {
    view.setOnClickListener {
        chatUsers?.let {
            if(it.isNotEmpty()){
                val action = ProfileFragmentDirections.actionProfileFragmentToChatsListFragment()
                Navigation.findNavController(view).navigate(action)
            }else{
                Toast.makeText(view.context, "No chats yet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// get image from Image Library of device
fun convertImageDataToBitmap(activity: Activity, data: Intent?): Bitmap? {
    val selectedImage = data?.data
    var bitmap: Bitmap? = null

    try {
        bitmap = if(Build.VERSION.SDK_INT < 28){
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
        }else{
            val source = selectedImage?.let {
                ImageDecoder.createSource(activity.contentResolver,
                    it
                )
            }
            source?.let { ImageDecoder.decodeBitmap(it) }
        }

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
    query.findInBackground{ objects, e ->
        if (e == null) {
            if (objects.isNotEmpty()) {
                textView.text = objects[0].username
            }
        } else {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("android:bindUserImg")
fun loadUserImgById(imageView: ImageView, userId: String) {
    val query: ParseQuery<ParseUser> = ParseUser.getQuery()
    // exclude user of this device
    query.whereEqualTo("objectId", userId)
    query.findInBackground{ objects, e ->
        if (e == null) {
            if (objects.isNotEmpty()) {
                val userImgUrl: String = objects[0].get("profileImg").toString()
                imageView.loadImage(userImgUrl, getProgressDrawable(imageView.context))
            }
        } else {
            e.printStackTrace()
        }
    }
}

// convert json to array
fun <T> jsonToArray(jArray: JSONArray?): MutableList<T> {
    val convertedArray = mutableListOf<T>()

    if (jArray != null) {
        for (i in 0 until jArray.length()) {
            @Suppress("UNCHECKED_CAST")
            convertedArray.add(jArray[i] as T)
        }
    }
    return convertedArray
}

// convert Date to String
@BindingAdapter("android:bindDate")
fun dateToStr(textView: TextView, date: Date){
    val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", US)
    textView.text = df.format(date)
}

//set user profile image
@BindingAdapter("android:bindProfileImageByUserId")
fun setProfileImgByUserId(imageView: ImageView, userId: String){
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereEqualTo("objectId", userId)
    query.getFirstInBackground { `object`, e ->
        if(e == null){
            `object`?.let {
                loadBindingImage(imageView, `object`.get("profileImg").toString())
            }
        }
    }
}