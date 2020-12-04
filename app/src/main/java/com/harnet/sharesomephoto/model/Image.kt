package com.harnet.sharesomephoto.model

import java.util.*

data class Image(val authorId: String){
    lateinit var imageId: String // for users list blinking reduction
    lateinit var imageURL: String
    lateinit var createAt: Date
}