package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.Image
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery

class FeedsViewModel(application: Application) : BaseViewModel(application) {
    val mFeeds = MutableLiveData<List<Image>>()
    val mIsImageLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    fun refresh() {
        getImagesFromParseServer()
    }

    // retrieve images
    private fun retrieveImages(imagesFromParse: List<Image>) {
        // set received list to observable mutable list
        mFeeds.postValue(imagesFromParse)
        // switch off error message
        mIsImageLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // get images from server
    private fun getImagesFromParseServer() {
        // clean previous version of feeds
        mFeeds.postValue(mutableListOf())

        val usersImages = mutableListOf<Image>()

        val query = ParseQuery<ParseObject>("Image")
        query.orderByDescending("createdAt")

        query.findInBackground(FindCallback { objects, parseObjectError ->
            if (parseObjectError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        //TODO AUTHOR NAme it should be
                        val imageForBind = Image("author")
                        imageForBind.imageURL = parseFile.url
                        usersImages.add(imageForBind)
                    }
                    retrieveImages(usersImages)
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
