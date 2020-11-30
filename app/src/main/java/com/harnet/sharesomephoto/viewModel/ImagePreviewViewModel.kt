package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.parse.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.lang.Exception

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
    fun sendImgToParseServer(sendError_ImagePreview: View, chosenImage: Bitmap, isProfileImage: Boolean) {
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
                    //TODO
                        if(isProfileImage){
                            setLastSentImg(sendError_ImagePreview.context)
                            Toast.makeText(
                                sendError_ImagePreview.context,
                                "Profile image was changed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    Toast.makeText(
                        sendError_ImagePreview.context,
                        "Image has been shared",
                        Toast.LENGTH_SHORT
                    ).show()
                    mIsImageSent.setValue(true)
                } else {
                    sendError_ImagePreview.visibility = View.VISIBLE
                    Toast.makeText(
                        sendError_ImagePreview.context,
                        "Image didn't send ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            })
        }
    }

    //get the last added image as profile's
    fun setLastSentImg(context: Context){
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("authorId", ParseUser.getCurrentUser().objectId)
        query.orderByDescending("createdAt")
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    //TODO here the last added image
                    setProfileImgToUser(context, objects[0].objectId)
                }
            } else {
                e.printStackTrace()
            }
        })
    }

    // addProfileImage
    fun setProfileImgToUser(context: Context, imgId: String){
        val parserUser = ParseUser.getCurrentUser()
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
            if(e == null){
                parserUser.put("profileImg", imgId)
                parserUser.saveInBackground()
            }else{
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}