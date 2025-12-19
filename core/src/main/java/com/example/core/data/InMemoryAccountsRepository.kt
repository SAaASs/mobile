package com.example.core.data

import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
            return Result.failure(IllegalArgumentException("Счёт с номером ${account.number} уже существует"))
        }
        map[account.number] = account
        emit()
        Result.success(Unit)
    }

    override suspend fun update(oldNumber: String, newAccount: BankAccount): Result<Unit> = mutex.withLock {
        if (!map.containsKey(oldNumber)) {
            return Result.failure(IllegalArgumentException("Счёт $oldNumber не найден"))
        }
        if (newAccount.number != oldNumber && map.containsKey(newAccount.number)) {
            return Result.failure(IllegalArgumentException("Счёт с номером ${newAccount.number} уже существует"))
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
