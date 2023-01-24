package com.example.naturepalestinesociety.models.profile

import com.example.naturepalestinesociety.models.post.Post

data class Profile(
    val bio: String,
    val country: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val phone_verified_at: Any,
    val photo: String,
    val token: String,
    val posts: List<Post>
):java.io.Serializable