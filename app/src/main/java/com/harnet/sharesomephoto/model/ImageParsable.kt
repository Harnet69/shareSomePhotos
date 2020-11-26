package com.harnet.sharesomephoto.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.parse.*
import java.io.ByteArrayOutputStream


interface ImageParsable {

    // send chosen image to Parse server
    fun sendImageToParseServer(
        context: Context?,
        chosenImage: Bitmap,
        isProfileImage: Boolean,
        profileImageView: ImageView?
    ) {
        val stream = ByteArrayOutputStream()
        // set an image to the appropriate format
        chosenImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        //TODO think about generating names for files
        //create a Parse file from the image
        val parseFile = ParseFile("image.png", byteArray)
        // Create Parse Image class
        val imageParseObj = ParseObject("Image")
        // attach an image
        imageParseObj.put("image", parseFile)
        // attach the user who uploading the file
        imageParseObj.put("username", ParseUser.getCurrentUser().username)
        // is the image Profile's
        if (isProfileImage) {
            imageParseObj.put("isProfileImg", true)
        } else {
            imageParseObj.put("isProfileImg", false)
        }

        imageParseObj.saveInBackground(SaveCallback { e ->
            if (e == null) {
                Log.i("ImageHandling", "sendImageToParseServer: $isProfileImage")
                if (isProfileImage) {
                    profileImageView?.let {
                        setProfileImage(profileImageView)
                    }
                } else {
                    refreshImagesGallery(context)
                }
                Toast.makeText(context, "Image has been shared", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Smth went wrong with sharing", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun refreshImagesGallery(context: Context?) {
        //TODO refresh gallery when it will be implemented
        Toast.makeText(context, "Refresh images gallery", Toast.LENGTH_SHORT).show()
    }

    // get Profile image and set it to Profile's image
    fun setProfileImage(profileImageView: ImageView) {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("username", ParseUser.getCurrentUser().username)
        query.whereEqualTo("isProfileImg", true)
        Log.i("ImageHandling", "setProfileImage $query")
        query.findInBackground(FindCallback { objects, parseQueryError ->
            if (parseQueryError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        parseFile.getDataInBackground { data, parseFileError ->
                            if (data != null && parseFileError == null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                // set image to Profile ImageView
                                Log.i("ImageHandling", " Bitmap: $bitmap")
                                profileImageView.setImageBitmap(bitmap)
                            } else {
                                Toast.makeText(
                                    profileImageView.context,
                                    parseFileError.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        profileImageView.context,
                        "Any profiles pictures",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}