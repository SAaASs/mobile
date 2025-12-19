package com.example.core.data

import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.core.domain.error.AccountFailure
class InMemoryAccountsRepository : AccountsRepository {

    private val mutex = Mutex()
    private val map = LinkedHashMap<String, BankAccount>()
    private val state = MutableStateFlow<List<BankAccount>>(emptyList())

    override fun observeAll(): Flow<List<BankAccount>> = state

    private fun emit() {
        state.value = map.values.toList()
    }

    override suspend fun add(account: BankAccount): Result<Unit> = mutex.withLock {
        if (map.containsKey(account.number)) {
            return Result.failure(AccountFailure.DuplicateNumber(account.number))
        }
        map[account.number] = account
        emit()
        Result.success(Unit)
    }

    override suspend fun update(oldNumber: String, newAccount: BankAccount): Result<Unit> = mutex.withLock {
        if (!map.containsKey(oldNumber)) {
            return Result.failure(AccountFailure.NotFound(oldNumber))
        }
        if (newAccount.number != oldNumber && map.containsKey(newAccount.number)) {
            return Result.failure(AccountFailure.DuplicateNumber(newAccount.number))
        }

        map.remove(oldNumber)
        map[newAccount.number] = newAccount
        emit()
        Result.success(Unit)
    }

    override suspend fun delete(number: String): Boolean = mutex.withLock {
        val removed = map.remove(number) != null
        if (removed) emit()
        removed
    }
}
