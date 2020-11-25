package com.harnet.sharesomephoto.service

import android.app.Application
import com.harnet.sharesomephoto.R
import com.parse.Parse
import com.parse.ParseACL

class ParseServiceAPI: Application() {

    override fun onCreate() {
        super.onCreate()

        // Enable Local Datastore
        Parse.enableLocalDatastore(this)

        // Initialization code
        Parse.initialize(
            Parse.Configuration.Builder(applicationContext)
                .applicationId(resources.getString(R.string.applicationId))
                .clientKey(resources.getString(R.string.clientKey))
                .server(resources.getString(R.string.server))
                .build()
        )


        // Test Parse server
//        val tweetObject = ParseObject("Feed")
//        tweetObject.put("name", "Hallil")
//        tweetObject.put("tweet", "Hello World")
//        tweetObject.put("scores", 20)
//        // send to a Parse server
//        tweetObject.saveInBackground(SaveCallback { e ->
//            if (e == null) {
//                Log.i("tweet", "insertTweet: ")
//            } else {
//                Log.i("tweet", "insertTweet: Smth wrong " + e.printStackTrace())
//            }
//        })

        // create user automatically if app don't have login system
//        ParseUser.enableAutomaticUser()

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}