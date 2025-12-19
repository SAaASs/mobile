package com.example.console

import com.example.core.domain.usecase.AccountUseCases
import com.example.core.factory.AccountFactorySelector
import com.example.core.input.InputParser
import com.example.core.input.InputValidator

class AccountInputProcessor(
    private val input: InputProvider,
    private val parser: InputParser,
    private val validator: InputValidator,
    private val factorySelector: AccountFactorySelector,
    private val useCases: AccountUseCases
) {
    suspend fun processInput() {
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

            val r = useCases.add(account)
            if (r.isFailure) {
                println("Ошибка: ${r.exceptionOrNull()?.message}")
            }
        }
    }
}
