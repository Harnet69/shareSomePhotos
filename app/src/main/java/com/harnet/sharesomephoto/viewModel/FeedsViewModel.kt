package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.Image
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery

class FeedsViewModel(application: Application) : BaseViewModel(application) {
    val mImages = MutableLiveData<List<Image>>()
    val mIsImageLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    fun refresh() {
        getImagesFromParseServer()
    }

    // retrieve images
    private fun retrieveImages(imagesFromParse: List<Image>) {
        // set received list to observable mutable list
        mImages.postValue(imagesFromParse)
        // switch off error message
        mIsImageLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // get images from server
    private fun getImagesFromParseServer() {
        // clean previous version of feeds
        mImages.postValue(mutableListOf())

        val usersImages = mutableListOf<Image>()

        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("isProfileImg", false)
        query.orderByDescending("createdAt")

        query.findInBackground(FindCallback { objects, parseObjectError ->
            if (parseObjectError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        usersImages.add(Image(parseFile.url))
                        retrieveImages(usersImages)
                    }
                } else {
                    mIsLoading.postValue(false)
                    mIsImageLoadError.postValue(false)
//                    Toast.makeText(getApplication(), "No images yet", Toast.LENGTH_SHORT).show()
                }
            } else {
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsImageLoadError.postValue(true)
                Toast.makeText(getApplication(), parseObjectError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
