package com.harnet.sharesomephoto.model

import android.content.Context
import android.widget.Toast
import com.parse.FindCallback
import com.parse.GetCallback
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser

interface ProfileImageable {

    // set Profile's image
    fun setProfileImage(context: Context) {
        getLastSentImg(context)
    }

    //get the last added image
    private fun getLastSentImg(context: Context) {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("authorId", ParseUser.getCurrentUser().objectId)
        query.orderByDescending("createdAt")
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    //TODO here the last added image
                    val imgId = objects[0].objectId
                    getImgUrlByImgId(context, imgId)
                }
            } else {
                e.printStackTrace()
            }
        })
    }

    // get image url by image id
    private fun getImgUrlByImgId(context: Context, imgId: String) {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("objectId", imgId)

        query.findInBackground(FindCallback { objects, parseObjectError ->
            if (parseObjectError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        setImgUrlToUser(context, parseFile.url.toString())
                    }
                }
            } else {
                parseObjectError.printStackTrace()
                Toast.makeText(context, parseObjectError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // add image url to user on a server
    private fun setImgUrlToUser(context: Context, imgUrl: String) {
        val parserUser = ParseUser.getCurrentUser()
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.getInBackground(parserUser.objectId, GetCallback { `object`, e ->
            if (e == null) {
                parserUser.put("profileImg", imgUrl)
                parserUser.saveInBackground()
                Toast.makeText(context, "Profile image was changed", Toast.LENGTH_SHORT)
                    .show()
            } else {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}