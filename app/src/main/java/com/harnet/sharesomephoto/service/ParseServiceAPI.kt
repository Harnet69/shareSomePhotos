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

        // create user automatically if app don't have login system
//        ParseUser.enableAutomaticUser()

        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }
}