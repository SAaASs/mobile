package com.example.app2.data


import com.example.app2.data.local.room.AccountDao
import com.example.app2.data.local.room.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomAccountDataSource @Inject constructor(
    private val dao: AccountDao
) {
    fun observeAll(): Flow<List<AccountEntity>> = dao.observeAll()

    fun exists(number: String): Boolean = dao.exists(number)
    fun insert(entity: AccountEntity) = dao.insert(entity)
    fun update(entity: AccountEntity): Int = dao.update(entity)
    fun delete(number: String): Int = dao.deleteByNumber(number)
    fun replace(oldNumber: String, entity: AccountEntity): Boolean = dao.replace(oldNumber, entity)
}
