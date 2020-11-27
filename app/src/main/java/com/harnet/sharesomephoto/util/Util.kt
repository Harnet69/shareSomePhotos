package com.harnet.sharesomephoto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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

// go to Image page from feeds
@BindingAdapter("android:goToImagePage", "android:username")
fun goToImagePage(view: ImageView, imageUrl: String?, username: String) {
    // prevent a crash when two items were clicked in the same time
    fun View.setOnSingleClickListener(l: (View) -> Unit) {
        setOnClickListener(OnSingleClickListenerService(l))
    }

    view.setOnSingleClickListener {imageView ->
        // navigate to appropriate detail fragment
        imageUrl?.let {
            val action = FeedsFragmentDirections.actionFeedsFragmentToImageFragment(imageUrl, username)
            // send image url to Image fragment
            Navigation.findNavController(imageView).navigate(action)
        }
    }
}