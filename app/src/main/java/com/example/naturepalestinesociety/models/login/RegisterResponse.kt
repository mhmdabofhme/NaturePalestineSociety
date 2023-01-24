package com.example.naturepalestinesociety.models.login

data class RegisterResponse(
    val country_id: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val is_featured: Int,
    val name_ar: String,
    val name_en: String,
    val updated_at: String
)