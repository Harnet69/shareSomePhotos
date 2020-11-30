package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.Image
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.SaveCallback
import java.io.ByteArrayOutputStream

class ImagePreviewViewModel(application: Application) : BaseViewModel(application) {
    val mImage = MutableLiveData<Bitmap>()

    val mIsLoadError = MutableLiveData<Boolean>()
    val mIsSendError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mIsImageSent = MutableLiveData<Boolean>()

    fun refresh(image: Bitmap){
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
    fun sendImgToParseServer(view: View, chosenImage: Bitmap) {
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
                mIsImageSent.setValue(true)
                Toast.makeText(view.context, "Image has been sent", Toast.LENGTH_SHORT).show()
            } else {
                mIsImageSent.setValue(false)
                e.printStackTrace()
                Toast.makeText(view.context, "Image didn't send ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}