package com.example.naturepalestinesociety.models.post

data class PaginationResponse<T>(
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val items: List<T>,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)