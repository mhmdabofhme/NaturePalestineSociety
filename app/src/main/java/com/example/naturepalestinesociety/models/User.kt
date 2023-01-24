package com.example.naturepalestinesociety.models

data class User(

//    val bio_ar: String,
//    val bio_en: String,
//    val country_id: String,
//    val created_at: String,
//    val email: String,
//    val email_verified_at: String?,
//    val id: Int,
//    val is_featured: Int,
//    val name_ar: String,
//    val name_en: String,
//    val phone: String,
//    val photo: String,
//    val updated_at: String

    val bio: String?,
    val country: String,
    val email: String,
    val id: Int,
    val feature: Int,
    val name: String,
    val photo: String,
    val fcm_token: String

):java.io.Serializable