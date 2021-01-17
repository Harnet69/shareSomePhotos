package com.harnet.sharesomephoto.model

import java.util.*

data class Message(
    val id: String? = null,
    val senderId: String,
    val recipientId: String,
    val text: String,
    val createAt: Date? = null,
    val isRead: Boolean
)