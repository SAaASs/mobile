package com.example.app2.data.local.room


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY number")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE number = :number LIMIT 1")
    fun getByNumber(number: String): AccountEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM accounts WHERE number = :number)")
    fun exists(number: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: AccountEntity)

    @Update
    fun update(entity: AccountEntity): Int

    @Query("DELETE FROM accounts WHERE number = :number")
    fun deleteByNumber(number: String): Int

    @Transaction
    fun replace(oldNumber: String, newEntity: AccountEntity): Boolean {
        val deleted = deleteByNumber(oldNumber)
        insert(newEntity) // если номер уже занят — упадёт и транзакция откатится
        return deleted > 0
    }
}
