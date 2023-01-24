package com.example.naturepalestinesociety.models

data class BaseResponseArray<T>(
    val data: List<T>? = null,
    val message: Int,
    val status: Boolean
)