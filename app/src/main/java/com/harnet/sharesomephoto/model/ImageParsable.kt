package com.harnet.sharesomephoto.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.harnet.sharesomephoto.util.getProgressDrawable
import com.harnet.sharesomephoto.util.loadImage
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
                if (isProfileImage) {
                    //TODO check if user have had a Profile image already
//                    makeImgNotProfiles()

                    profileImageView?.let {
                        setProfileImage(profileImageView)
                    }
                } else {
                    refreshImagesGallery(context)
                }
                Toast.makeText(context, "Image has been shared", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Smth went wrong with sharing ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //get profile image of user
    fun getProfileImgByUser(userName: String, userImageView: ImageView) {
            val query = ParseQuery<ParseObject>("Image")
            query.whereEqualTo("username", userName)
            query.whereEqualTo("isProfileImg", true)

            query.findInBackground(FindCallback { objects, parseObjectError ->
                if (parseObjectError == null) {
                    if (objects.isNotEmpty()) {
                        for (image in objects) {
                            val parseFile = image.getParseFile("image")
                            userImageView.loadImage(
                                parseFile.url,
                                getProgressDrawable(userImageView.context)
                            )
                        }
                    } else {
                        Log.i("userImages", "No users with images")
                    }
                } else {
                    parseObjectError.printStackTrace()
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

        query.findInBackground(FindCallback { objects, parseQueryError ->
            if (parseQueryError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        //set image to user field on Parse Server
                        setProfileImageUrl(profileImageView.context, parseFile.url)

                        profileImageView.loadImage(parseFile.url, getProgressDrawable(profileImageView.context))
                    }
                } else {
                    Toast.makeText(
                        profileImageView.context,
                        "Any profiles pictures",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(
                    profileImageView.context,parseQueryError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    //reset isProfilePictures flag from all pictures
    fun makeImgNotProfiles() {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("username", ParseUser.getCurrentUser().username)
        query.whereEqualTo("isProfileImg", true)
        query.findInBackground(FindCallback { objects, parseQueryError ->
            if (parseQueryError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        image.put("isProfileImg", false)
                        image.saveInBackground()
                    }
                }
            } else {
                parseQueryError.printStackTrace()
            }
        })
    }

    fun setProfileImgNew(profileImageView: ImageView){
        val parserUser = ParseUser.getCurrentUser()
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
            if(e == null){
                profileImageView.loadImage(`object`.get("profileImg").toString(), getProgressDrawable(profileImageView.context))
                Toast.makeText(profileImageView.context, `object`.get("profileImg").toString(), Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(profileImageView.context, e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // addProfileImage
    fun setProfileImageUrl(context: Context, imgUrl: String){
        val parserUser = ParseUser.getCurrentUser()
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
            if(e == null){
                parserUser.put("profileImg", imgUrl)
                parserUser.saveInBackground()
            }else{
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}