package com.example.naturepalestinesociety.models.login

data class Register(
    val name_ar: String,
    val name_en: String,
    val email: String,
    val password: String,
    val country_id: String,
    val fcm_token: String
)
