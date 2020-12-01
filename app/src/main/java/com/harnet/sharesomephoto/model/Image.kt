package com.harnet.sharesomephoto.model

import java.util.*

data class Image(val authorId: String){
    lateinit var imageURL: String
    lateinit var createAt: Date
}