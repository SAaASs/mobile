package com.example.app2.data

import android.database.sqlite.SQLiteConstraintException
import com.example.app2.data.local.room.toDomain
import com.example.app2.data.local.room.toEntity
import com.example.core.model.BankAccount
import com.example.core.storage.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomAccountRepository @Inject constructor(
    private val ds: RoomAccountDataSource
) : AccountRepository {

    // удобно для UI, чтобы список сам обновлялся
    fun observeAll(): Flow<List<BankAccount>> =
        ds.observeAll().map { list -> list.map { it.toDomain() } }

    override fun add(account: BankAccount): Result<Unit> = try {
        if (ds.exists(account.number)) {
            Result.failure(IllegalArgumentException("Счёт с номером ${account.number} уже существует"))
        } else {
            ds.insert(account.toEntity())
            Result.success(Unit)
        }
    } catch (e: SQLiteConstraintException) {
        Result.failure(IllegalArgumentException("Счёт с номером ${account.number} уже существует"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun update(number: String, newAccount: BankAccount): Result<Unit> = try {
        val newNumber = newAccount.number
        val entity = newAccount.toEntity()

        val ok = if (newNumber == number) {
            ds.update(entity) > 0
        } else {
            if (ds.exists(newNumber)) {
                return Result.failure(IllegalArgumentException("Счёт с номером $newNumber уже существует"))
            }
            ds.replace(number, entity)
        }

        if (ok) Result.success(Unit)
        else Result.failure(IllegalArgumentException("Счёт $number не найден"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun delete(number: String): Boolean =
        ds.delete(number) > 0

    // Эти методы из core-интерфейса лучше НЕ дергать из UI напрямую.
    // Оставляем для совместимости (можно не использовать в app).
    override fun get(number: String): BankAccount? = null
    override fun getAll(): List<BankAccount> = emptyList()
}
