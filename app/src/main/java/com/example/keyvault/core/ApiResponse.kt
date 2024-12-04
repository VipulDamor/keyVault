package com.example.keyvault.core

sealed interface ApiResponse<out R> {
    data class Success<out T>(val data: T) : ApiResponse<T>
    data class Error(val message: String) : ApiResponse<Nothing>
}