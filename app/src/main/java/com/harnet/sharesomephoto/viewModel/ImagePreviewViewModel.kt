package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.Toast
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.SaveCallback
import java.io.ByteArrayOutputStream

class ImagePreviewViewModel(application: Application) : BaseViewModel(application) {

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
        imageParseObj.put("authorId", ParseUser.getCurrentUser().username)


        imageParseObj.saveInBackground(SaveCallback { e ->
            if (e == null) {
                Toast.makeText(view.context, "Image has been shared", Toast.LENGTH_SHORT).show()
            } else {
                e.printStackTrace()
                Toast.makeText(view.context, "Smth went wrong with sharing ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}