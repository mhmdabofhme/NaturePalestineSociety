package com.example.naturepalestinesociety.models.login

data class LoginResponse(
    val bio_ar: String,
    val bio_en: String,
    val country_id: String,
    val email: String,
    val phone: String,
    val email_verified_at: String?,
    val id: Int,
    val name_ar: String,
    val name_en: String,
    val created_at: String?,
    val updated_at: String?,
    val photo: String,
    val is_featured: Int
)