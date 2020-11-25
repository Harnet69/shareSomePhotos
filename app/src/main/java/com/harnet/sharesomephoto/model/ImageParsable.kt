package com.harnet.sharesomephoto.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.SaveCallback
import java.io.ByteArrayOutputStream

interface ImageParsable {

    // send chosen image to Parse server
    fun sendImageToParseServer(context: Context?, chosenImage: Bitmap, isProfileImage: Boolean) {
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
//            Log.i("sendImageToParseServer", "sendImageToParseServer Profile photo: $chosenImage")
        } else {
            imageParseObj.put("isProfileImg", false)
//            Log.i("sendImageToParseServer", "sendImageToParseServer Feed photo: $chosenImage")
        }

        imageParseObj.saveInBackground(SaveCallback {e ->
            if(e == null){
                Toast.makeText(context, "Image has been shared", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Smth went wrong with sharing", Toast.LENGTH_SHORT).show()
            }
        })
    }
}