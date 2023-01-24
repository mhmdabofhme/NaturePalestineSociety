package com.example.naturepalestinesociety.models.post

import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.project.Project

data class Post(
    val description: String,
    val email_verified_at: Any,
    val id: Int,
    val lang: Double,
    val lat: Double,
    val media: List<Media>,
    val phone_verified_at: Any,
    val project: Project,
    val title: String,
    val token: String,
    val created_at: String,
    val user: User
):java.io.Serializable