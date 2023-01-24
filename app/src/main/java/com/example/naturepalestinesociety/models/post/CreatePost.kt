package com.example.naturepalestinesociety.models.post

import java.io.File

data class CreatePost(
    val title_ar: String,
    val title_en: String,
    val description_ar: String,
    val description_en: String,
    val lat: String,
    val lang: String,
    val project_id: String,
    val images: File?,
)
