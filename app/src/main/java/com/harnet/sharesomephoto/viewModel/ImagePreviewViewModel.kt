package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.ProfileImageable
import com.parse.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ImagePreviewViewModel(application: Application) : BaseViewModel(application), ProfileImageable {
    val mImage = MutableLiveData<Bitmap>()
    val mImgUrl = MutableLiveData<String>()

    val mIsLoadError = MutableLiveData<Boolean>()
    val mIsSendError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mIsImageSent = MutableLiveData<Boolean>()

    fun refresh(image: Bitmap) {
        retrieveImage(image)
    }

    // retrieve image
    private fun retrieveImage(imageFromParse: Bitmap) {
        // set received list to observable mutable list
        mImage.postValue(imageFromParse)
        // switch off error message
        mIsLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // send chosen image to Parse server
    // TODO think about move to a separate User interface
    fun sendImgToParseServer(
        sendErrorImagePreview: View,
        loadingProgressBar: ProgressBar,
        chosenImage: Bitmap,
        isProfileImage: Boolean
    ) {
        launch {
            val stream = ByteArrayOutputStream()
            // set an image to the appropriate format
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val byteArray = stream.toByteArray()
            //create a Parse file from the image
            val parseFile = ParseFile("image.jpg", byteArray)
            // Create Parse Image class
            val imageParseObj = ParseObject("Image")
            // attach an image
            imageParseObj.put("image", parseFile)
            // attach the userId who uploading the file
            imageParseObj.put("authorId", ParseUser.getCurrentUser().objectId)

            imageParseObj.saveInBackground(SaveCallback { e ->
                if (e == null) {
                    if (isProfileImage) {
                        // set Profile image
                        setProfileImage(getApplication())
                        mIsImageSent.setValue(true)
                    } else {
                        Toast.makeText(
                            sendErrorImagePreview.context,
                            "Image have been shared",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    sendErrorImagePreview.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                }else{
                    e.printStackTrace()
                    sendErrorImagePreview.visibility = View.VISIBLE
                    Toast.makeText(
                        sendErrorImagePreview.context,
                        "Image didn't send ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
