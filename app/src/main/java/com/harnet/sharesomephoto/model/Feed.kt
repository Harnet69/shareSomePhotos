package com.harnet.sharesomephoto.model

data class Feed(val imgId: String, val description: String, val authorId: String){
    val likes: Int = 0
}
