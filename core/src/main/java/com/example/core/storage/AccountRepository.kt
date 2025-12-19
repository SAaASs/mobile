package com.example.core.storage


import  com.example.core.model.BankAccount

interface AccountRepository {
    fun add(account: BankAccount): Result<Unit>
    fun update(number: String, newAccount: BankAccount): Result<Unit>
    fun delete(number: String): Boolean

    fun get(number: String): BankAccount?
    fun getAll(): List<BankAccount>
}

class InMemoryAccountRepository : AccountRepository {
    private val map = LinkedHashMap<String, BankAccount>()

    override fun add(account: BankAccount): Result<Unit> {
        if (map.containsKey(account.number)) {
            return Result.failure(IllegalArgumentException("Счёт с номером ${account.number} уже существует"))
        }
        map[account.number] = account
        return Result.success(Unit)
    }

    override fun update(number: String, newAccount: BankAccount): Result<Unit> {
        if (!map.containsKey(number)) {
            return Result.failure(IllegalArgumentException("Счёт $number не найден"))
        }
        // если меняют номер — проверяем конфликт
        if (newAccount.number != number && map.containsKey(newAccount.number)) {
            return Result.failure(IllegalArgumentException("Счёт с номером ${newAccount.number} уже существует"))
        }
        map.remove(number)
        map[newAccount.number] = newAccount
        return Result.success(Unit)
    }

    override fun delete(number: String): Boolean = map.remove(number) != null

    override fun get(number: String): BankAccount? = map[number]
    override fun getAll(): List<BankAccount> = map.values.toList()
}
