package com.harnet.sharesomephoto.model

import java.util.*

data class Message(
    val senderId: String,
    val recipientId: String,
    val text: String,
    val createAt: Date? = null
)