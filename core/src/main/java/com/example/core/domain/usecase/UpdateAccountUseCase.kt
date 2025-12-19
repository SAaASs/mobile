package com.example.core.domain.usecase

import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount

class UpdateAccountUseCase(private val repo: AccountsRepository) {
    suspend operator fun invoke(oldNumber: String, newAccount: BankAccount): Result<Unit> =
        repo.update(oldNumber, newAccount)
}
