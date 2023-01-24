package com.example.naturepalestinesociety.messages

class ChatMessage(
    val id: String,
    val text: String?,
    val image: String?,
    val fromId: String,
    val toId: String,
    var seen: Boolean,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", "", false, -1)




}