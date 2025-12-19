package com.example.core.domain.repository

import com.example.core.model.BankAccount
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun observeAll(): Flow<List<BankAccount>>

    suspend fun add(account: BankAccount): Result<Unit>
    suspend fun update(oldNumber: String, newAccount: BankAccount): Result<Unit>
    suspend fun delete(number: String): Boolean
}