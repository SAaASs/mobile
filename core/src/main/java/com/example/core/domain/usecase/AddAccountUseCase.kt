package com.example.core.domain.usecase

import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount

class AddAccountUseCase(private val repo: AccountsRepository) {
    suspend operator fun invoke(account: BankAccount): Result<Unit> = repo.add(account)
}
