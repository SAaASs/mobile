package com.example.core.domain.usecase

import com.example.core.domain.repository.AccountsRepository

class DeleteAccountUseCase(private val repo: AccountsRepository) {
    suspend operator fun invoke(number: String): Boolean = repo.delete(number)
}
