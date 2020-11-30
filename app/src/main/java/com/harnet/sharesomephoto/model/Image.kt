package com.harnet.sharesomephoto.model

import java.util.*

data class Image(val authorId: String){
    lateinit var imageId: String
    lateinit var createAt: Date
}