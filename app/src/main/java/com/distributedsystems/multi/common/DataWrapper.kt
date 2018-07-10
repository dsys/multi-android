package com.distributedsystems.multi.common

data class DataWrapper<T> (
        val data: T? = null,
        val hasErrors : Boolean = false,
        val error: String? = null
)