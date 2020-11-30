package com.harnet.sharesomephoto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.service.OnSingleClickListenerService
import com.harnet.sharesomephoto.view.FeedsFragmentDirections
import com.harnet.sharesomephoto.view.ProfileFragmentDirections
import com.harnet.sharesomephoto.view.UsersFragmentDirections
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

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
@BindingAdapter("android:goToImagePage", "android:username")
fun goToImagePage(view: ImageView, imageUrl: String?, username: String?) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener { imageView ->
        imageUrl?.let {
            username?.let {
                when (imageView.tag.toString()) {
                    "profileFragment" -> {
                        val action = ProfileFragmentDirections.actionProfileFragmentToImageFragment(
                            imageUrl,
                            username
                        )
                        Navigation.findNavController(imageView).navigate(action)
                    }
                    "imageFragment" -> {
                        val action2 = FeedsFragmentDirections.actionFeedsFragmentToImageFragment(
                            imageUrl,
                            username
                        )
                        Navigation.findNavController(imageView).navigate(action2)
                    }
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

// convert Bitmap to String
fun convertBitMapToString(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
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