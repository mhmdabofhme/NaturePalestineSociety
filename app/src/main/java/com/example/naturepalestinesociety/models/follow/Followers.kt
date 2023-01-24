package com.example.naturepalestinesociety.models.follow

import com.example.naturepalestinesociety.models.User

data class Followers(
    val id: Int,
//    val targets: User
//    ,
    val users: User,
//    val featured: List<User>
)