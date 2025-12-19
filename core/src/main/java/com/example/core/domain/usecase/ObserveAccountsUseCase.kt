package com.example.core.domain.usecase

import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase(private val repo: AccountsRepository) {
    operator fun invoke(): Flow<List<BankAccount>> = repo.observeAll()
}
