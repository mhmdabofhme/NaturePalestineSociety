package com.example.naturepalestinesociety.models.notifications

data class Notification(
    val created_at: String,
    val data: NotificationBody,
    val id: Int,
    val read_at: String,
    val type: String
)