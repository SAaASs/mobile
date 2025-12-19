package com.example.core.domain.error

sealed class AccountFailure : Exception() {
    data class DuplicateNumber(val number: String) : AccountFailure()
    data class NotFound(val number: String) : AccountFailure()
}
