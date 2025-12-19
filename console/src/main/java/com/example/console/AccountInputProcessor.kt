package com.example.console


import com.example.core.factory.AccountFactorySelector
import com.example.core.input.InputParser
import com.example.core.input.InputValidator
import com.example.core.storage.AccountRepository

class AccountInputProcessor(
    private val input: InputProvider,
    private val parser: InputParser,
    private val validator: InputValidator,
    private val factorySelector: AccountFactorySelector,
    private val repo: AccountRepository
) {
    fun processInput() {
        println("Введите данные (пустая строка — завершить):")
        println("T <номер> <плата> | S <номер> <процент> <true/false>")

        while (true) {
            val line = input.readLine()?.trim().orEmpty()
            if (line.isEmpty()) break

            val parts = parser.parse(line)
            if (!validator.isValid(parts)) continue

            val account = factorySelector.create(parts)
            if (account == null) {
                println("Ошибка: некорректные данные.")
                continue
            }

            val res = repo.add(account)
            if (res.isFailure) println("Ошибка: ${res.exceptionOrNull()?.message}")
        }
    }
}
