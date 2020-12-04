package com.harnet.sharesomephoto.model

data class User(val name: String, val password: String, val email: String){
    lateinit var userIdForList: String // for users list blinking reduction
    lateinit var userId: String
    lateinit var profileImgUrl: String
}