package com.approw.core.types

class RestfulResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)
