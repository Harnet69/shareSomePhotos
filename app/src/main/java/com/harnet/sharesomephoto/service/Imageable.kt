package com.harnet.sharesomephoto.service

import android.util.Log

interface Imageable {

    fun chooseImage() {
        Log.i("ChooseImageFor Profile", "chooseImage: ChooseImageFor Profile")
    }
}