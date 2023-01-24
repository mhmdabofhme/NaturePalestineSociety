package com.example.naturepalestinesociety.models

data class AuthResponse<T> (
    val data: T? = null,
    val status: Boolean,
    val token: String,
)