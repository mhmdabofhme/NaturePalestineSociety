package com.example.naturepalestinesociety.models

data class BaseResponseObject<T>(
    val data: T? = null,
    val message: Int,
    val status: Boolean
)