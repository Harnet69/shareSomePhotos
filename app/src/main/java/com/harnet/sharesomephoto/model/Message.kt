package com.harnet.sharesomephoto.model

data class Message(
    val senderId: String,
    val recipientId: String,
    val text: String,
    val createAt: String? = null
)