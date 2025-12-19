package com.example.app2.data

import com.example.app2.data.local.room.toDomain
import com.example.app2.data.local.room.toEntity
import com.example.core.domain.repository.AccountsRepository
import com.example.core.model.BankAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomAccountRepository @Inject constructor(
    private val ds: RoomAccountDataSource
) : AccountsRepository {

    override fun observeAll(): Flow<List<BankAccount>> =
        ds.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(account: BankAccount): Result<Unit> = try {
        if (ds.exists(account.number)) {
            Result.failure(IllegalArgumentException("Счёт с номером ${account.number} уже существует"))
        } else {
            ds.insert(account.toEntity())
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun update(oldNumber: String, newAccount: BankAccount): Result<Unit> = try {
        val newNumber = newAccount.number
        val entity = newAccount.toEntity()

        val ok = if (newNumber == oldNumber) {
            ds.update(entity) > 0
        } else {
            if (ds.exists(newNumber)) {
                return Result.failure(IllegalArgumentException("Счёт с номером $newNumber уже существует"))
            }
            ds.replace(oldNumber, entity)
        }

        if (ok) Result.success(Unit)
        else Result.failure(IllegalArgumentException("Счёт $oldNumber не найден"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun delete(number: String): Boolean =
        ds.delete(number) > 0
}
