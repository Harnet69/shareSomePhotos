package com.harnet.sharesomephoto.model

data class User(val name: String, val password: String, val email: String){
    lateinit var userId: String
    lateinit var profileImgId: String
}