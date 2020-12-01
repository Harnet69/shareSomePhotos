package com.harnet.sharesomephoto.model

data class Feed(val imgUrl: String, val description: String, val authorName: String){
    val likes: Int = 0
}
